package com.weird.service.impl;

import com.weird.facade.RecordFacade;
import com.weird.mapper.main.DeckMapper;
import com.weird.mapper.main.UserDataMapper;
import com.weird.model.DeckDetailModel;
import com.weird.model.DeckListModel;
import com.weird.model.UserDataModel;
import com.weird.model.dto.DeckCardDTO;
import com.weird.model.dto.DeckInfoDTO;
import com.weird.model.dto.DeckListDTO;
import com.weird.model.enums.DeckCardTypeEnum;
import com.weird.model.param.DeckInfoParam;
import com.weird.model.param.DeckListParam;
import com.weird.model.param.DeckShareParam;
import com.weird.model.param.DeckSubmitParam;
import com.weird.service.DeckService;
import com.weird.utils.BeanConverter;
import com.weird.utils.OperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 卡组Service实现
 *
 * @author Nidhogg
 * @date 2021.8.14
 */
@Service
@Slf4j
public class DeckServiceImpl implements DeckService {
    @Autowired
    DeckMapper deckMapper;

    @Autowired
    UserDataMapper userDataMapper;

    @Autowired
    RecordFacade recordFacade;

    @Override
    public List<DeckListDTO> searchPage(DeckListParam param) throws Exception {
        return deckMapper.selectDeckList(param);
    }

    @Override
    public List<DeckListDTO> searchPageAdmin(DeckListParam param) throws Exception {
        return deckMapper.selectDeckListAdmin(param);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean addDeck(DeckSubmitParam param) throws Exception {
        UserDataModel user = userDataMapper.selectByNameInAllDistinct(param.getName());
        if (user == null) {
            throw new OperationException("找不到用户：[%s]！", param.getName());
        }

        DeckInfoDTO deck = param.getDeck();
        deck.setUserId(user.getUserId());
        deck.setLastModifyTime(System.currentTimeMillis());
        int addCount = deckMapper.addDeck(deck);
        int deckId = deck.getDeckId();
        if (addCount <= 0 || deckId <= 0) {
            return false;
        }

        addDeckDetailToDB(deck);
        recordFacade.setRecord(user.getUserName(), "创建了卡组[%s]", deck.getDeckName());
        return true;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean updateDeck(DeckSubmitParam param) throws Exception {
        UserDataModel user = userDataMapper.selectByNameInAllDistinct(param.getName());
        if (user == null) {
            throw new OperationException("找不到用户：[%s]！", param.getName());
        }

        DeckInfoDTO deck = param.getDeck();
        int deckId = deck.getDeckId();
        DeckListModel dbDeck = deckMapper.getDeckListInfoByDeckId(deckId);
        if (dbDeck == null || (dbDeck.getUserId() != user.getUserId() && user.getIsAdmin() == 0)) {
            throw new OperationException("该卡组无法修改！");
        }

        deck.setLastModifyTime(System.currentTimeMillis());
        deckMapper.updateDeck(deck);
        deckMapper.deleteCardByDeckId(deckId);

        addDeckDetailToDB(deck);
        String oldDeckName = dbDeck.getDeckName();
        String newDeckName = deck.getDeckName();
        if (deck.getUserId() == user.getUserId()) {
            if (Objects.equals(oldDeckName, newDeckName)) {
                recordFacade.setRecord(user.getUserName(), "修改了卡组[%s]", oldDeckName);
            } else {
                recordFacade.setRecord(user.getUserName(), "修改了卡组[%s]为[%s]", oldDeckName, newDeckName);
            }
        } else {
            UserDataModel deckOwner = userDataMapper.selectByPrimaryKey(dbDeck.getUserId());
            String deckOwnerName;
            if (deckOwner != null) {
                deckOwnerName = deckOwner.getUserName();
            } else {
                deckOwnerName = "Unknown";
            }
            if (Objects.equals(oldDeckName, newDeckName)) {
                recordFacade.setRecord(user.getUserName(), "修改了[%s]的卡组[%s]", deckOwnerName, oldDeckName);
            } else {
                recordFacade.setRecord(user.getUserName(), "修改了[%s]的卡组[%s]为[%s]", deckOwnerName, oldDeckName, newDeckName);
            }
        }
        return true;
    }

    private void addDeckDetailToDB(DeckInfoDTO deck) {
        int deckId = deck.getDeckId();
        List<DeckDetailModel> detailList = new ArrayList<>(90);
        detailList.addAll(getCardDetailInfo(deck.getMainList(), deckId, DeckCardTypeEnum.MAIN.getId()));
        detailList.addAll(getCardDetailInfo(deck.getExList(), deckId, DeckCardTypeEnum.EX.getId()));
        detailList.addAll(getCardDetailInfo(deck.getSideList(), deckId, DeckCardTypeEnum.SIDE.getId()));
        if (!CollectionUtils.isEmpty(detailList)) {
            deckMapper.batchAddDeckCard(detailList);
        }
    }

    private List<DeckDetailModel> getCardDetailInfo(List<DeckCardDTO> cardList, int deckId, int type) {
        List<DeckDetailModel> detailList = BeanConverter.convertList(cardList, DeckDetailModel.class);
        for (DeckDetailModel card : detailList) {
            card.setDeckId(deckId);
            card.setType(type);
        }
        return detailList;
    }

    @Override
    public DeckInfoDTO getDeckInfo(DeckInfoParam param, boolean isAdmin) throws Exception {
        UserDataModel user = userDataMapper.selectByNameInAllDistinct(param.getName());
        if (user == null) {
            throw new OperationException("找不到用户：[{}]！", param.getName());
        }
        int deckId = param.getDeckId();
        DeckListModel dbDeckListModel = deckMapper.getDeckListInfoByDeckId(deckId);
        if (dbDeckListModel == null || (dbDeckListModel.getUserId() != user.getUserId() && !isAdmin)) {
            throw new OperationException("无权查看此卡组！");
        }

        UserDataModel owner = userDataMapper.selectByPrimaryKey(dbDeckListModel.getUserId());
        DeckInfoDTO realDeck = BeanConverter.convert(dbDeckListModel, DeckInfoDTO.class);
        realDeck.setUserName(owner.getUserName());

        List<DeckDetailModel> deckDetailList = deckMapper.getDeckDetailByDeckId(deckId);
        if (!CollectionUtils.isEmpty(deckDetailList)) {
            Map<Integer, List<DeckDetailModel>> cardMapByType = deckDetailList.stream().collect(Collectors.groupingBy(DeckDetailModel::getType));
            realDeck.setMainList(BeanConverter.convertList(cardMapByType.getOrDefault(DeckCardTypeEnum.MAIN.getId(), Collections.emptyList()), DeckCardDTO.class));
            realDeck.setExList(BeanConverter.convertList(cardMapByType.getOrDefault(DeckCardTypeEnum.EX.getId(), Collections.emptyList()), DeckCardDTO.class));
            realDeck.setSideList(BeanConverter.convertList(cardMapByType.getOrDefault(DeckCardTypeEnum.SIDE.getId(), Collections.emptyList()), DeckCardDTO.class));
        }
        realDeck.buildYdk();

        return realDeck;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean renameDeck(DeckSubmitParam param) throws Exception {
        UserDataModel user = userDataMapper.selectByNameInAllDistinct(param.getName());
        if (user == null) {
            throw new OperationException("找不到用户：[%s]！", param.getName());
        }

        DeckInfoDTO deck = param.getDeck();
        int deckId = deck.getDeckId();
        DeckListModel dbDeck = deckMapper.getDeckListInfoByDeckId(deckId);
        if (dbDeck == null || (dbDeck.getUserId() != user.getUserId() && user.getIsAdmin() == 0)) {
            throw new OperationException("该卡组无法修改！");
        }

        DeckInfoDTO updateParam = new DeckInfoDTO();
        updateParam.setDeckId(deckId);
        updateParam.setDeckName(deck.getDeckName());
        updateParam.setLastModifyTime(System.currentTimeMillis());
        boolean result = deckMapper.updateDeck(updateParam) > 0;

        if (result) {
            String oldDeckName = dbDeck.getDeckName();
            String newDeckName = deck.getDeckName();
            if (deck.getUserId() == user.getUserId()) {
                if (Objects.equals(oldDeckName, newDeckName)) {
                    recordFacade.setRecord(user.getUserName(), "重命名了卡组[%s]", oldDeckName);
                } else {
                    recordFacade.setRecord(user.getUserName(), "重命名了卡组[%s]为[%s]", oldDeckName, newDeckName);
                }
            } else {
                UserDataModel deckOwner = userDataMapper.selectByPrimaryKey(dbDeck.getUserId());
                String deckOwnerName;
                if (deckOwner != null) {
                    deckOwnerName = deckOwner.getUserName();
                } else {
                    deckOwnerName = "Unknown";
                }
                if (Objects.equals(oldDeckName, newDeckName)) {
                    recordFacade.setRecord(user.getUserName(), "重命名了[%s]的卡组[%s]", deckOwnerName, oldDeckName);
                } else {
                    recordFacade.setRecord(user.getUserName(), "重命名了[%s]的卡组[%s]为[%s]", deckOwnerName, oldDeckName, newDeckName);
                }
            }
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean removeDeck(DeckSubmitParam param) throws Exception {
        UserDataModel user = userDataMapper.selectByNameInAllDistinct(param.getName());
        if (user == null) {
            throw new OperationException("找不到用户：[%s]！", param.getName());
        }

        DeckInfoDTO deck = param.getDeck();
        int deckId = deck.getDeckId();
        DeckListModel dbDeck = deckMapper.getDeckListInfoByDeckId(deckId);
        if (dbDeck == null || (dbDeck.getUserId() != user.getUserId() && user.getIsAdmin() == 0)) {
            throw new OperationException("该卡组无法删除！");
        }

        boolean result = deckMapper.deleteDeckByDeckId(deckId) + deckMapper.deleteCardByDeckId(deckId) > 0;
        if (result) {
            String deckName = dbDeck.getDeckName();
            if (deck.getUserId() == user.getUserId()) {
                recordFacade.setRecord(user.getUserName(), "删除了卡组[%s]", deckName);
            } else {
                UserDataModel deckOwner = userDataMapper.selectByPrimaryKey(dbDeck.getUserId());
                String deckOwnerName;
                if (deckOwner != null) {
                    deckOwnerName = deckOwner.getUserName();
                } else {
                    deckOwnerName = "Unknown";
                }
                recordFacade.setRecord(user.getUserName(), "删除了[%s]的卡组[%s]", deckOwnerName, deckName);
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public String shareDeck(DeckShareParam param, boolean isAdmin) throws Exception {
        UserDataModel user = userDataMapper.selectByNameInAllDistinct(param.getName());
        if (user == null) {
            throw new OperationException("找不到用户：[%s]！", param.getName());
        }

        DeckListModel dbDeck = deckMapper.getDeckListInfoByDeckId(param.getDeckId());
        if (dbDeck == null || (dbDeck.getUserId() != user.getUserId() && user.getIsAdmin() == 0)) {
            throw new OperationException("该卡组无法修改状态！");
        }

        if (dbDeck.getShare() == param.getShare()) {
            throw new OperationException("该卡组分享状态未改变！");
        }

        if (deckMapper.updateDeckShare(dbDeck.getDeckId(), param.getShare()) <= 0) {
            throw new OperationException("修改分享状态失败！");
        }

        String deckName = dbDeck.getDeckName();
        String operation;
        if (param.getShare() == 0) {
            operation = "取消分享";
        } else {
            operation = "分享";
        }
        if (dbDeck.getUserId() == user.getUserId()) {
            recordFacade.setRecord(user.getUserName(), "%s了卡组[%s]", operation, deckName);
        } else {
            UserDataModel deckOwner = userDataMapper.selectByPrimaryKey(dbDeck.getUserId());
            String deckOwnerName;
            if (deckOwner != null) {
                deckOwnerName = deckOwner.getUserName();
            } else {
                deckOwnerName = "Unknown";
            }
            recordFacade.setRecord(user.getUserName(), "%s了[%s]的卡组[%s]", operation, deckOwnerName, deckName);
        }

        return operation + "成功！";
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public void updateDeckCardWhenRenamed(long oldCode, long newCode, int newType) {
        deckMapper.updateDeckCodeStepA(oldCode, newCode, newType);
        deckMapper.updateDeckCodeStepB(oldCode, newCode);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public void updateDeckCardCountWhenUpdateCount(String userName, long cardCode, int newCount) {
        UserDataModel user = userDataMapper.selectByNameInAllDistinct(userName);
        if (user != null) {
            updateDeckCardCountWhenUpdateCount(user.getUserId(), cardCode, newCount);
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public void updateDeckCardCountWhenUpdateCount(long userId, long cardCode, int newCount) {
        List<DeckDetailModel> dbList = deckMapper.getDetailWhenChangeCount(userId, cardCode, newCount);
        if (!CollectionUtils.isEmpty(dbList)) {
            List<Long> pkList = dbList.stream().map(DeckDetailModel::getCode).collect(Collectors.toList());
            if (newCount > 0) {
                deckMapper.updateDeckCardCount(newCount, pkList);
            } else {
                deckMapper.deleteDeckCardCount(pkList);
            }
        }
    }

    @Override
    public DeckInfoDTO getDeckById(int deckId) {
        DeckListModel dbDeckListModel = deckMapper.getDeckListInfoByDeckId(deckId);
        if (dbDeckListModel == null) {
            return null;
        }

        DeckInfoDTO realDeck = BeanConverter.convert(dbDeckListModel, DeckInfoDTO.class);

        List<DeckDetailModel> deckDetailList = deckMapper.getDeckDetailByDeckId(deckId);
        if (!CollectionUtils.isEmpty(deckDetailList)) {
            Map<Integer, List<DeckDetailModel>> cardMapByType = deckDetailList.stream().collect(Collectors.groupingBy(DeckDetailModel::getType));
            realDeck.setMainList(BeanConverter.convertList(cardMapByType.getOrDefault(DeckCardTypeEnum.MAIN.getId(), Collections.emptyList()), DeckCardDTO.class));
            realDeck.setExList(BeanConverter.convertList(cardMapByType.getOrDefault(DeckCardTypeEnum.EX.getId(), Collections.emptyList()), DeckCardDTO.class));
            realDeck.setSideList(BeanConverter.convertList(cardMapByType.getOrDefault(DeckCardTypeEnum.SIDE.getId(), Collections.emptyList()), DeckCardDTO.class));
        }
        return realDeck;
    }
}
