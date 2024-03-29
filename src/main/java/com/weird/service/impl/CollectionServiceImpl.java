package com.weird.service.impl;

import com.weird.facade.RecordFacade;
import com.weird.mapper.main.CollectionMapper;
import com.weird.mapper.main.PackageCardMapper;
import com.weird.mapper.main.UserCardListMapper;
import com.weird.mapper.main.UserDataMapper;
import com.weird.model.PackageCardModel;
import com.weird.model.UserDataModel;
import com.weird.model.enums.CollectionOperationEnum;
import com.weird.model.enums.LoginTypeEnum;
import com.weird.model.param.CollectionParam;
import com.weird.service.CollectionService;
import com.weird.utils.OperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 用户收藏服务实现
 *
 * @author Nidhogg
 * @date 2021.7.28
 */
@Service
@Slf4j
public class CollectionServiceImpl implements CollectionService {
    @Autowired
    UserDataMapper userDataMapper;

    @Autowired
    CollectionMapper collectionMapper;

    @Autowired
    PackageCardMapper packageCardMapper;

    @Autowired
    RecordFacade recordFacade;

    @Autowired
    UserCardListMapper userCardListMapper;

    @Override
    public List<Integer> getCollectionByName(String userName) throws OperationException {
        UserDataModel userModel = userDataMapper.selectByNameDistinct(userName);
        if (userModel == null) {
            throw new OperationException("找不到该用户:[%s]！", userName);
        }

        List<Integer> collectionList = collectionMapper.getCollectionIdByUserId(userModel.getUserId());
        if (collectionList == null) {
             return Collections.emptyList();
        }

        return collectionList;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean operation(CollectionParam param, LoginTypeEnum loginTypeEnum) throws OperationException {
        String userName = param.getName();
        UserDataModel userModel = userDataMapper.selectByNameDistinct(userName);
        if (userModel == null) {
            throw new OperationException("找不到该用户:[%s]！", userName);
        }
        int userId = userModel.getUserId();

        String cardName = param.getCardName();
        PackageCardModel card = packageCardMapper.selectByNameDistinct(cardName);
        if (card == null) {
            throw new OperationException("找不到该卡片！");
        }
        int cardPk = card.getCardPk();

        List<Integer> existCheck = collectionMapper.checkCollection(userId, cardPk);

        switch (Objects.requireNonNull(CollectionOperationEnum.getById(param.getOp()))) {
            case ADD:
                if (LoginTypeEnum.NORMAL.equals(loginTypeEnum)) {
                    List<Integer> visibleCardPkList = userCardListMapper.getVisibleCardPkList();
                    if (!visibleCardPkList.contains(cardPk)) {
                        throw new OperationException("找不到该卡片！");
                    }
                }
                if (!CollectionUtils.isEmpty(existCheck)) {
                    throw new OperationException("重复收藏！");
                }
                if (collectionMapper.addCollection(userId, cardPk) > 0) {
                    recordFacade.setRecord(userName, "[%s]将[%s]添加为收藏", userName, cardName);
                    return true;
                } else {
                    return false;
                }
            case REMOVE:
                if (CollectionUtils.isEmpty(existCheck)) {
                    throw new OperationException("未收藏该卡片！");
                }
                if (collectionMapper.delCollection(userId, cardPk) > 0) {
                    recordFacade.setRecord(userName, "[%s]将[%s]从收藏移除", userName, cardName);
                    return true;
                } else {
                    return false;
                }
            default:
                throw new OperationException("操作类型有误！");
        }
    }
}
