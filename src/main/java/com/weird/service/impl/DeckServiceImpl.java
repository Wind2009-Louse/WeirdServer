package com.weird.service.impl;

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
import com.weird.model.param.DeckSubmitParam;
import com.weird.service.DeckService;
import com.weird.utils.BeanConverter;
import com.weird.utils.OperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

        DeckInfoDTO realDeck = BeanConverter.convert(dbDeckListModel, DeckInfoDTO.class);
        realDeck.setUserName(user.getUserName());
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
        return deckMapper.updateDeck(updateParam) > 0;
    }

    @Override
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

        return deckMapper.deleteDeckByDeckId(deckId) + deckMapper.deleteCardByDeckId(deckId) > 0;
    }
}
