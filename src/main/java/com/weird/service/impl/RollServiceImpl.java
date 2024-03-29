package com.weird.service.impl;

import com.alibaba.fastjson.JSON;
import com.weird.config.AutoConfig;
import com.weird.config.WeirdConfig;
import com.weird.facade.BroadcastFacade;
import com.weird.facade.RecordFacade;
import com.weird.mapper.main.*;
import com.weird.model.*;
import com.weird.model.dto.RollDetailDTO;
import com.weird.model.dto.RollListDTO;
import com.weird.model.enums.DustEnum;
import com.weird.model.enums.RollStatusEnum;
import com.weird.model.param.SearchRollParam;
import com.weird.service.CardPreviewService;
import com.weird.service.CardService;
import com.weird.service.RollService;
import com.weird.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

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
    CardService cardService;

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

    @Autowired
    RecordFacade recordFacade;

    @Autowired
    BroadcastFacade broadcastFacade;

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
        List<PackageCardModel> rareCardList = new ArrayList<>(3);
        for (PackageCardModel card : cardModels) {
            RollDetailModel rollDetailModel = new RollDetailModel();
            rollDetailModel.setRollId(rollId);
            int cardPk = card.getCardPk();
            rollDetailModel.setCardPk(cardPk);
            rollDetailModel.setIsDust((byte) 0);
            rollDetailModel.setCardName(card.getCardName());
            rollDetailModel.setRare(card.getRare());

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
                rareCardList.add(card);
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
        boolean isNormalRoll = cardModels.size() == 3;
        int lastNonawardCount = userModel.getNonawardCount();
        String firstAwardHint;
        if (!isNormalRoll) {
            firstAwardHint = "";
        } else {
            boolean firstRare = false;
            if (!CollectionUtils.isEmpty(rareCardList)) {
                int nonawardCount = userModel.getNonawardCount();
                userModel.setNonawardCount(nonawardCount - nonawardCount % WeirdConfig.fetchNonAwardLimit());
                if (userModel.getDailyAward() <= 0) {
                    firstRare = true;
                    userModel.setDailyAward(1);
                    firstAwardHint = String.format("，这也是%s今天的第一张闪卡", userModel.getUserName());
                } else {
                    firstAwardHint = "";
                }
            } else {
                firstAwardHint = "";
                userModel.setNonawardCount(userModel.getNonawardCount() + 1);
            }

            // 更新抽卡计数和转盘次数
            int roulette = userModel.getRoulette();
            int rollCount = userModel.getRollCount();
            int dailyRoll = userModel.getDailyRoll();
            rollCount += 1;
            dailyRoll += 1;
            final int rollCountToRoulette = WeirdConfig.fetchRollCountToRoulette();
            if (rollCount >= rollCountToRoulette) {
                final int newRouletteCount = rollCount / rollCountToRoulette;
                roulette += newRouletteCount;
                rollCount %= rollCountToRoulette;
                broadcastFacade.sendMsgAsync(String.format("【广播】%s 获得了 %d 次转盘的机会！", userModel.getUserName(), newRouletteCount));
            }
            userModel.setRoulette(roulette);
            userModel.setRollCount(rollCount);
            userModel.setDailyRoll(dailyRoll);

            // 判断dp变化
            if (AutoConfig.fetchDp()) {
                int duelPoint = userModel.getDuelPoint();
                if (dailyRoll == AutoConfig.fetchDailyRollCondition()) {
                    duelPoint += AutoConfig.fetchDailyRollDp();
                }
                if (firstRare) {
                    duelPoint += AutoConfig.fetchDailyRareDp();
                }
                userModel.setDuelPoint(duelPoint);
            }
        }

        // 更新用户数据
        if (userDataMapper.updateByPrimaryKey(userModel) <= 0) {
            throw new OperationException("更新用户数据失败！");
        }

        // 广播
        String info = "";

        // 月见黑提示
        int currentNonawardCount = userModel.getNonawardCount();
        if (currentNonawardCount > 0) {
            switch (currentNonawardCount % WeirdConfig.fetchNonAwardLimit()) {
                case 90:
                    info = String.format("【广播】%s 的月见黑已经达到了 %d，再接再厉！", userModel.getUserName(), currentNonawardCount);
                    break;
                case 0:
                    info = String.format("【广播】功夫不负有心人，%s 的月见黑达到了 %d！", userModel.getUserName(), currentNonawardCount);
                    break;
                default:
                    break;
            }
        }

        // 闪卡提示
        if (rareCardList.size() == 1) {
            PackageCardModel card = rareCardList.get(0);
            final String rareCardName = card.getCardName();
            int ownCount = userCardListMapper.selectCardOwnCount(card.getCardPk());
            int selfOwnCount = userCardListMapper.selectCardOwnCountByUser(card.getCardPk(), userModel.getUserId());

            if (isNormalRoll) {
                info = String.format("【广播】可喜可贺，%s 在 %d 月见黑时，抽到了第%d/%d张[%s]%s%s！",
                        userModel.getUserName(),
                        lastNonawardCount,
                        selfOwnCount,
                        ownCount,
                        card.getRare(),
                        rareCardName,
                        firstAwardHint);
            } else {
                info = String.format("【广播】恭喜 %s 抽到了全服第%d/%d张[%s]%s%s！",
                        userModel.getUserName(),
                        selfOwnCount,
                        ownCount,
                        card.getRare(),
                        rareCardName,
                        firstAwardHint);
            }
        }

        if (!StringUtils.isEmpty(info)) {
            broadcastFacade.sendMsgAsync(info);
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
            allRollList = rollListMapper.selectByParam(param.getPackageNameList(), param.getUserNameList(), startString, endString, param.getCardName());
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
                RollDetailDTO detailDTO = new RollDetailDTO();
                detailDTO.setCardName(rollCardModel.getCardName());
                detailDTO.setRare(rollCardModel.getRare());
                detailDTO.setIsDust(rollCardModel.getIsDust());

                // 根据卡名查询效果详细
                if (rollList.size() < CardPreviewUtil.HIDE_PREVIEW_COUNT) {
                    CardPreviewModel previewModel = cardPreviewService.selectPreviewByName(rollCardModel.getCardName());
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
    public boolean roll(List<String> cardNames, String userName, String operator) throws Exception {
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

        int dustCount = userModel.getDustCount();
        int nonawardCount = userModel.getNonawardCount();
        int rollCount = userModel.getRollCount();
        int roulette = userModel.getRoulette();
        addCards(userModel, cardModels, rollModel.getRollId());

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[%s]抽卡：", userName));
        for (PackageCardModel card : cardModels) {
            sb.append(String.format("[%s](%s), ", card.getCardName(), card.getRare()));
        }
        sb.append(String.format("尘:%d->%d，月见黑:%d->%d，抽卡计数:%d->%d，转盘次数:%d->%d",
                dustCount, userModel.getDustCount(),
                nonawardCount, userModel.getNonawardCount(),
                rollCount, userModel.getRollCount(),
                roulette, userModel.getRoulette()));
        recordFacade.setRecord(operator, sb.toString());
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
    public boolean setStatus(long rollId, int newStatus, String operator) throws Exception {
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

        recordFacade.setRecord(operator,
                "[%s]的状态变为%s", JSON.toJSONString(rollListModel), RollStatusEnum.getById(newStatus));
        if (rollListMapper.updateByPrimaryKey(rollListModel) <= 0) {
            throw new OperationException("修改抽卡记录状态失败！");
        }
        if (newStatus == RollStatusEnum.VALID.getId()) {
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
            userModel.setRollCount(userModel.getRollCount() - 1);
        }
        if (userDataMapper.updateByPrimaryKey(userModel) <= 0) {
            throw new OperationException("更新用户信息失败！");
        }

        clearCardOwnListCache();
        clearRollListWithDetailCache();
        return true;
    }
}
