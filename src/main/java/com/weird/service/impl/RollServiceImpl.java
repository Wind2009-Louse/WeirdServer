package com.weird.service.impl;

import com.weird.mapper.*;
import com.weird.model.*;
import com.weird.model.dto.RollDetailDTO;
import com.weird.model.dto.RollListDTO;
import com.weird.service.RollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Service
public class RollServiceImpl implements RollService {
    @Autowired
    RollListMapper rollListMapper;

    @Autowired
    RollDetailMapper rollDetailMapper;

    @Autowired
    PackageInfoMapper packageInfoMapper;

    @Autowired
    PackageCardMapper packageCardMapper;

    @Autowired
    UserDataMapper userDataMapper;

    @Autowired
    UserCardListMapper userCardListMapper;

    final List<String> NR_RARE = Arrays.asList("N","R");

    /**
     * 将抽卡内容添加到用户上
     *
     * @param userModel 用户
     * @param cardModels 新卡
     * @param rollId 抽卡记录ID（禁用-适用时为0）
     * @return 添加是否成功
     */
    private boolean addCards(UserDataModel userModel,
                             List<PackageCardModel> cardModels,
                             long rollId) throws Exception{
        int userId = userModel.getUserId();

        // 每张卡判断是否变尘
        int addDust = 0;
        boolean awarded = false;
        for (PackageCardModel card : cardModels){
            RollDetailModel rollDetailModel = new RollDetailModel();
            rollDetailModel.setRollId(rollId);
            int cardPk = card.getCardPk();
            rollDetailModel.setCardPk(cardPk);
            rollDetailModel.setIsDust((byte) 0);

            // 获取当前拥有的数量
            UserCardListModel cardCountModel = userCardListMapper.selectByUserCard(userId, cardPk);
            boolean needInsert = false;
            if (cardCountModel == null){
                needInsert = true;
                cardCountModel = new UserCardListModel();
                cardCountModel.setUserId(userId);
                cardCountModel.setCardPk(cardPk);
                cardCountModel.setCount(0);
            }

            // 变尘
            int ownCount = cardCountModel.getCount();
            if (ownCount >= 3){
                rollDetailModel.setIsDust((byte) 1);
                if (NR_RARE.contains(card.getRare())){
                    addDust ++;
                } else {
                    addDust += 50;
                }
            } else {
                cardCountModel.setCount(ownCount+1);
            }

            // 月见黑
            if (!NR_RARE.contains(card.getRare())){
                awarded = true;
            }

            // 写回数据库
            if (needInsert){
                if (userCardListMapper.insert(cardCountModel) <= 0){
                    throw new Exception(String.format("插入[%s]的卡片数量时出错！", card.getCardName()));
                }
            } else {
                if (userCardListMapper.update(cardCountModel) <= 0){
                    throw new Exception(String.format("更新[%s]的卡片数量时出错！", card.getCardName()));
                }
            }

            if (rollId > 0 && rollDetailMapper.insert(rollDetailModel) <= 0){
                throw new Exception(String.format("插入[%s]的抽卡数量时出错！", card.getCardName()));
            }
        }

        if (addDust > 0){
            userModel.setDustCount(userModel.getDustCount() + addDust);
        }

        // 非正常抽卡不算月见黑
        if (cardModels.size() == 3) {
            if (awarded){
                userModel.setNonawardCount(0);
            } else {
                userModel.setNonawardCount(userModel.getNonawardCount() + 1);
            }
        }

        // 更新用户数据
        if (userDataMapper.updateByPrimaryKey(userModel) <= 0){
            throw new Exception("更新用户数据失败！");
        }

        return true;
    }

    /**
     * 根据用户名查找抽卡结果（只包含记录，结果未返回）
     *
     * @param packageName 卡包名
     * @param userName 用户名
     * @return 结果列表（内容需要查询）
     */
    @Override
    public List<RollListDTO> selectRollList(String packageName, String userName) {
        return rollListMapper.selectByParam(packageName, userName);
    }

    /**
     * 根据抽卡结果返回详细内容
     *
     * @param list 抽卡结果
     * @return 带详细内容的抽卡结果
     */
    @Override
    public List<RollListDTO> selectRollDetail(List<RollListDTO> list) throws Exception {
        List<RollListDTO> result = new LinkedList<>();
        for (RollListDTO dto: list){
            List<RollDetailModel> cardList = rollDetailMapper.selectCardPkById(dto.getRollId());
            if (cardList == null || cardList.size() == 0){
                throw new Exception("抽卡结果查询失败！");
            }
            List<RollDetailDTO> cardResult = new LinkedList<>();
            for (RollDetailModel rollCardModel : cardList){
                int cardPk = rollCardModel.getCardPk();
                PackageCardModel cardModel = packageCardMapper.selectByPrimaryKey(cardPk);
                if (cardModel == null){
                    throw new Exception(String.format("卡片%d查询失败！", cardPk));
                }
                RollDetailDTO detailDTO = new RollDetailDTO();
                detailDTO.setCardName(cardModel.getCardName());
                detailDTO.setRare(cardModel.getRare());
                cardResult.add(detailDTO);
            }
            dto.setRollResult(cardResult);
            result.add(dto);
        }
        return result;
    }

    /**
     * 抽卡处理
     *
     * @param packageName 卡包名
     * @param cardNames 卡片名
     * @param userName 用户名
     * @return 是否记录成功
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean roll(String packageName, List<String> cardNames, String userName) throws Exception {
        // 判断输入
        PackageInfoModel packageModel = packageInfoMapper.selectByName(packageName);
        if (packageModel == null){
            throw new Exception(String.format("找不到该卡包：%s！", packageName));
        }
        int packageId = packageModel.getPackageId();
        List<PackageCardModel> cardModels = new LinkedList<>();
        for (String cardName : cardNames){
            PackageCardModel card = packageCardMapper.selectInPackageDistinct(packageId, cardName);
            if (card == null){
                throw new Exception(String.format("找不到该卡片：%s！", cardName));
            }
            cardModels.add(card);
        }
        UserDataModel userModel = userDataMapper.selectByNameDistinct(userName);
        if (userModel == null){
            throw new Exception(String.format("找不到该用户：%s！", userName));
        }
        int userId = userModel.getUserId();

        // 插入抽卡记录
        RollListModel rollModel = new RollListModel();
        rollModel.setRollPackageId(packageId);
        rollModel.setRollUserId(userId);
        rollModel.setIsDisabled((byte) 0);
        if (rollListMapper.insert(rollModel) <= 0){
            throw new Exception("添加抽卡记录失败！");
        }

        addCards(userModel, cardModels, rollModel.getRollId());

        return true;
    }

    /**
     * 设置抽卡记录状态
     *
     * @param rollId 抽卡记录ID
     * @param newStatus 新的状态
     * @return 是否设置成功
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean setStatus(long rollId, int newStatus) throws Exception {
        // 获取抽卡记录
        RollListModel rollListModel = rollListMapper.selectByPrimaryKey(rollId);
        if (rollListModel == null){
            throw new Exception("获取抽卡记录失败！");
        }
        if (rollListModel.getIsDisabled() == newStatus){
            throw new Exception("抽卡记录状态没有被修改！");
        }

        // 获取用户
        UserDataModel userModel = userDataMapper.selectByPrimaryKey(rollListModel.getRollUserId());
        if (userModel == null){
            throw new Exception("找不到抽卡的用户！");
        }
        int userId = userModel.getUserId();

        // 获取抽卡内容
        List<RollDetailModel> rollDetailModelList = rollDetailMapper.selectCardPkById(rollId);
        if (rollDetailModelList == null || rollDetailModelList.size() == 0){
            throw new Exception("当前抽卡记录无抽卡详情！");
        }

        // 获取卡片
        List<PackageCardModel> cardModels = new LinkedList<>();
        for (RollDetailModel cardDetail : rollDetailModelList){
            PackageCardModel card = packageCardMapper.selectByPrimaryKey(cardDetail.getCardPk());
            if (card == null){
                throw new Exception(String.format("找不到该卡片：%s！", cardDetail.getCardPk()));
            }
            cardModels.add(card);
        }

        if (newStatus == 0){
            addCards(userModel, cardModels, 0);
            return true;
        }

        // 回滚
        int dustCount = userModel.getDustCount();
        for (int index = 0; index < cardModels.size(); ++ index) {
            RollDetailModel rollDetail = rollDetailModelList.get(index);
            PackageCardModel cardModel = cardModels.get(index);
            // 抽的是尘，减尘
            if (rollDetail.getIsDust() == 1){
                if (NR_RARE.contains(cardModel.getRare())){
                    dustCount --;
                } else {
                    dustCount -= 50;
                }
            } else {
                // 减少对应的卡片数量
                UserCardListModel cardListModel = userCardListMapper.selectByUserCard(userId, cardModel.getCardPk());
                if (cardListModel == null || cardListModel.getCount() == 0){
                    throw new Exception(String.format("[%s]不拥有[%s]！", userModel.getUserName(), cardModel.getCardName()));
                }
                cardListModel.setCount(cardListModel.getCount() - 1);
                if (userCardListMapper.update(cardListModel) <= 0){
                    throw new Exception("回滚抽卡记录失败！");
                }
            }
        }

        userModel.setDustCount(dustCount);
        if (cardModels.size() == 3){
            userModel.setNonawardCount(userModel.getNonawardCount()-1);
        }
        if (userDataMapper.updateByPrimaryKey(userModel) <= 0){
            throw new Exception("更新用户信息失败！");
        }
        
        return true;
    }
}
