package com.weird.service.impl;

import com.weird.mapper.main.*;
import com.weird.model.*;
import com.weird.model.dto.RollDetailDTO;
import com.weird.model.dto.RollListDTO;
import com.weird.model.enums.DustEnum;
import com.weird.model.param.SearchRollParam;
import com.weird.service.CardPreviewService;
import com.weird.service.RollService;
import com.weird.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static com.weird.utils.CacheUtil.clearCardOwnListCache;
import static com.weird.utils.CacheUtil.clearRollListWithDetailCache;

/**
 * RollService实现
 *
 * @author Nidhogg
 * @date 2020.9.5
 */
@Service
@Slf4j
public class RollServiceImpl implements RollService {
    @Autowired
    RollListMapper rollListMapper;

    @Autowired
    RollDetailMapper rollDetailMapper;

    @Autowired
    PackageCardMapper packageCardMapper;

    @Autowired
    UserDataMapper userDataMapper;

    @Autowired
    UserCardListMapper userCardListMapper;

    @Autowired
    CardPreviewService cardPreviewService;

    /**
     * 将抽卡内容添加到用户上
     *
     * @param userModel  用户
     * @param cardModels 新卡
     * @param rollId     抽卡记录ID（禁用-适用时为0）
     * @return 添加是否成功
     */
    private boolean addCards(UserDataModel userModel,
                             List<PackageCardModel> cardModels,
                             long rollId) throws Exception {
        int userId = userModel.getUserId();

        // 每张卡判断是否变尘
        int addDust = 0;
        boolean awarded = false;
        for (PackageCardModel card : cardModels) {
            RollDetailModel rollDetailModel = new RollDetailModel();
            rollDetailModel.setRollId(rollId);
            int cardPk = card.getCardPk();
            rollDetailModel.setCardPk(cardPk);
            rollDetailModel.setIsDust((byte) 0);

            // 获取当前拥有的数量
            UserCardListModel cardCountModel = userCardListMapper.selectByUserCard(userId, cardPk);
            boolean needInsert = false;
            if (cardCountModel == null) {
                needInsert = true;
                cardCountModel = new UserCardListModel();
                cardCountModel.setUserId(userId);
                cardCountModel.setCardPk(cardPk);
                cardCountModel.setCount(0);
            }

            // 变尘
            int ownCount = cardCountModel.getCount();
            if (ownCount >= 3 && PackageUtil.NR_LIST.contains(card.getRare())) {
                rollDetailModel.setIsDust((byte) 1);
                addDust += DustEnum.GET_NR.getCount();
            } else {
                cardCountModel.setCount(ownCount + 1);
            }

            // 月见黑
            if (!PackageUtil.NR_LIST.contains(card.getRare())) {
                awarded = true;
            }

            // 写回数据库
            if (needInsert) {
                if (userCardListMapper.insert(cardCountModel) <= 0) {
                    throw new OperationException("插入[%s]的卡片数量时出错！", card.getCardName());
                }
            } else {
                if (userCardListMapper.update(cardCountModel) <= 0) {
                    throw new OperationException("更新[%s]的卡片数量时出错！", card.getCardName());
                }
            }

            if (rollId > 0 && rollDetailMapper.insert(rollDetailModel) <= 0) {
                throw new OperationException("插入[%s]的抽卡数量时出错！", card.getCardName());
            }
        }

        if (addDust > 0) {
            userModel.setDustCount(userModel.getDustCount() + addDust);
        }

        // 非正常抽卡不算月见黑
        if (cardModels.size() == 3) {
            if (awarded) {
                int nonawardCount = userModel.getNonawardCount();
                userModel.setNonawardCount(nonawardCount - nonawardCount % 100);
            } else {
                userModel.setNonawardCount(userModel.getNonawardCount() + 1);
            }
        }

        // 更新用户数据
        if (userDataMapper.updateByPrimaryKey(userModel) <= 0) {
            throw new OperationException("更新用户数据失败！");
        }

        return true;
    }

    /**
     * 根据卡包名和用户名查找抽卡结果
     *
     * @param param 参数
     * @return 结果列表
     */
    @Override
    public PageResult<RollListDTO> selectRollList(SearchRollParam param) throws Exception {
        // 命中缓存，直接返回
        String totalKey = param.toString();
        log.debug("查询抽卡记录列表：{}", totalKey);
        PageResult<RollListDTO> cache = CacheUtil.getRollListWithDetailCache(totalKey);
        if (cache != null) {
            return cache;
        }

        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startString = null;
        if (param.getStartTime() > 0) {
            startString = fm.format(new Date(param.getStartTime() * 1000));
        }
        String endString = null;
        if (param.getEndTime() > 0) {
            endString = fm.format(new Date(param.getEndTime() * 1000));
        }

        // 通过分页截取需要查询详细内容的部分
        int page = param.getPage();
        int pageSize = param.getPageSize();
        param.setPage(0);
        param.setPageSize(0);
        String subKey = param.toString();
        List<RollListDTO> allRollList = CacheUtil.getRollListCache(subKey);
        if (allRollList == null) {
            allRollList = rollListMapper.selectByParam(param.getPackageNameList(), param.getUserNameList(), startString, endString);
            CacheUtil.putRollListCache(subKey, allRollList);
        }

        PageResult<RollListDTO> resultList = new PageResult<>();
        resultList.addPageInfo(allRollList, page, pageSize);
        List<RollListDTO> rollList = resultList.getDataList();

        // 为每一条信息查询卡片内容
        for (RollListDTO rollListDTO : rollList) {
            // 查询卡片ID列表
            List<RollDetailModel> cardList = rollDetailMapper.selectCardPkById(rollListDTO.getRollId());
            if (cardList == null || cardList.size() == 0) {
                throw new OperationException("抽卡结果[%d]查询失败！", rollListDTO.getRollId());
            }

            List<RollDetailDTO> cardResult = new LinkedList<>();
            for (RollDetailModel rollCardModel : cardList) {
                // 根据ID查询卡名和稀有度
                int cardPk = rollCardModel.getCardPk();
                PackageCardModel cardModel = packageCardMapper.selectByPrimaryKey(cardPk);
                if (cardModel == null) {
                    throw new OperationException("卡片[%d]查询失败！", cardPk);
                }
                RollDetailDTO detailDTO = new RollDetailDTO();
                detailDTO.setCardName(cardModel.getCardName());
                detailDTO.setRare(cardModel.getRare());
                detailDTO.setIsDust(rollCardModel.getIsDust());

                // 根据卡名查询效果详细
                if (rollList.size() < CardPreviewUtil.HIDE_PREVIEW_COUNT) {
                    CardPreviewModel previewModel = cardPreviewService.selectPreviewByName(cardModel.getCardName());
                    if (previewModel != null) {
                        detailDTO.setDesc(CardPreviewUtil.getPreview(previewModel));
                        detailDTO.setPicId(previewModel.getId());
                    }
                }
                cardResult.add(detailDTO);
            }
            rollListDTO.setRollResult(cardResult);
        }

        // 返回结果
        resultList.setDataList(rollList);
        CacheUtil.putRollListWithDetailCache(totalKey, resultList);
        return resultList;
    }

    /**
     * 抽卡处理
     *
     * @param cardNames 卡片名
     * @param userName  用户名
     * @return 是否记录成功
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean roll(List<String> cardNames, String userName) throws Exception {
        // 判断用户
        UserDataModel userModel = userDataMapper.selectByNameDistinct(userName);
        if (userModel == null) {
            throw new OperationException("找不到该用户：%s！", userName);
        }
        int userId = userModel.getUserId();

        // 判断输入
        List<PackageCardModel> cardModels = new LinkedList<>();
        int packageId = 0;
        for (String cardName : cardNames) {
            PackageCardModel card = packageCardMapper.selectByNameDistinct(cardName);
            if (card == null) {
                throw new OperationException("抽卡记录[%s]中找不到该卡片：%s！", cardNames, cardName);
            }
            if (packageId == 0) {
                packageId = card.getPackageId();
            } else if (card.getPackageId() != packageId) {
                throw new OperationException("抽卡记录[%s]中卡片不在同一卡包！", cardNames);
            }
            cardModels.add(card);
        }

        // 插入抽卡记录
        RollListModel rollModel = new RollListModel();
        rollModel.setRollPackageId(packageId);
        rollModel.setRollUserId(userId);
        rollModel.setIsDisabled((byte) 0);
        if (rollListMapper.insert(rollModel) <= 0) {
            throw new OperationException("添加抽卡记录失败！");
        }

        addCards(userModel, cardModels, rollModel.getRollId());

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[%s]抽卡：", userName));
        for (PackageCardModel card : cardModels) {
            sb.append(String.format("[%s](%s), ", card.getCardName(), card.getRare()));
        }
        sb.append(String.format("当前尘=%d，月见黑=%d", userModel.getDustCount(), userModel.getNonawardCount()));
        log.warn(sb.toString());
        clearCardOwnListCache();
        clearRollListWithDetailCache();

        return true;
    }

    /**
     * 设置抽卡记录状态
     *
     * @param rollId    抽卡记录ID
     * @param newStatus 新的状态
     * @return 是否设置成功
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean setStatus(long rollId, int newStatus) throws Exception {
        // 获取抽卡记录
        RollListModel rollListModel = rollListMapper.selectByPrimaryKey(rollId);
        if (rollListModel == null) {
            throw new OperationException("获取抽卡记录失败！");
        }
        if (rollListModel.getIsDisabled() == newStatus) {
            throw new OperationException("抽卡记录状态没有被修改！");
        }

        // 获取用户
        UserDataModel userModel = userDataMapper.selectByPrimaryKey(rollListModel.getRollUserId());
        if (userModel == null) {
            throw new OperationException("找不到抽卡的用户！");
        }
        int userId = userModel.getUserId();

        // 获取抽卡内容
        List<RollDetailModel> rollDetailModelList = rollDetailMapper.selectCardPkById(rollId);
        if (rollDetailModelList == null || rollDetailModelList.size() == 0) {
            throw new OperationException("当前抽卡记录无抽卡详情！");
        }

        // 获取卡片
        List<PackageCardModel> cardModels = new LinkedList<>();
        for (RollDetailModel cardDetail : rollDetailModelList) {
            PackageCardModel card = packageCardMapper.selectByPrimaryKey(cardDetail.getCardPk());
            if (card == null) {
                throw new OperationException("找不到该卡片：[%s]！", cardDetail.getCardPk());
            }
            cardModels.add(card);
        }

        rollListModel.setIsDisabled((byte) newStatus);
        log.warn("[{}]的状态变为{}", rollListModel, newStatus);
        if (rollListMapper.updateByPrimaryKey(rollListModel) <= 0) {
            throw new OperationException("修改抽卡记录状态失败！");
        }
        if (newStatus == 0) {
            addCards(userModel, cardModels, 0);
            clearCardOwnListCache();
            clearRollListWithDetailCache();
            return true;
        }

        // 回滚
        int dustCount = userModel.getDustCount();
        for (int index = 0; index < cardModels.size(); ++index) {
            RollDetailModel rollDetail = rollDetailModelList.get(index);
            PackageCardModel cardModel = cardModels.get(index);
            // 抽的是尘，减尘
            if (rollDetail.getIsDust() == 1) {
                if (PackageUtil.NR_LIST.contains(cardModel.getRare())) {
                    dustCount -= DustEnum.GET_NR.getCount();
                } else {
                    dustCount -= DustEnum.GET_RARE.getCount();
                }
            } else {
                // 减少对应的卡片数量
                UserCardListModel cardListModel = userCardListMapper.selectByUserCard(userId, cardModel.getCardPk());
                if (cardListModel == null || cardListModel.getCount() == 0) {
                    throw new OperationException("用户[%s]不拥有卡片[%s]！", userModel.getUserName(), cardModel.getCardName());
                }
                cardListModel.setCount(cardListModel.getCount() - 1);
                if (userCardListMapper.update(cardListModel) <= 0) {
                    throw new OperationException("回滚抽卡记录失败！");
                }
            }
        }

        userModel.setDustCount(dustCount);
        if (cardModels.size() == 3) {
            userModel.setNonawardCount(userModel.getNonawardCount() - 1);
        }
        if (userDataMapper.updateByPrimaryKey(userModel) <= 0) {
            throw new OperationException("更新用户信息失败！");
        }

        clearCardOwnListCache();
        clearRollListWithDetailCache();
        return true;
    }
}
