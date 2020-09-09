package com.weird.service.impl;

import com.weird.mapper.PackageCardMapper;
import com.weird.mapper.PackageInfoMapper;
import com.weird.mapper.UserCardListMapper;
import com.weird.mapper.UserDataMapper;
import com.weird.model.PackageCardModel;
import com.weird.model.PackageInfoModel;
import com.weird.model.UserCardListModel;
import com.weird.model.UserDataModel;
import com.weird.model.dto.CardListDTO;
import com.weird.model.dto.CardOwnListDTO;
import com.weird.service.CardService;
import com.weird.utils.OperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CardServiceImpl implements CardService {
    @Autowired
    PackageInfoMapper packageInfoMapper;

    @Autowired
    PackageCardMapper packageCardMapper;

    @Autowired
    UserDataMapper userDataMapper;

    @Autowired
    UserCardListMapper userCardListMapper;

    /**
     * 修改用户持有的卡片数量
     *
     * @param userName    用户名
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param count       新的卡片数量
     * @return 是否修改成功
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean updateCardCount(String userName, String packageName, String cardName, int count) throws Exception {
        if (count > 3 || count < 0) {
            throw new OperationException("修改卡片数量错误，应在0~3内！");
        }

        UserDataModel userModel = userDataMapper.selectByNameDistinct(userName);
        if (userModel == null) {
            throw new OperationException("找不到该用户:[%s]！", userName);
        }

        PackageInfoModel packageModel = packageInfoMapper.selectByNameDistinct(packageName);
        if (packageModel == null) {
            throw new OperationException("找不到该卡包：[%s]！", packageName);
        }
        PackageCardModel cardModel = packageCardMapper.selectInPackageDistinct(packageModel.getPackageId(), cardName);
        if (cardModel == null) {
            throw new OperationException("找不到该卡片：[%s]！", cardName);
        }

        int userId = userModel.getUserId();
        int cardPk = cardModel.getCardPk();

        UserCardListModel model = userCardListMapper.selectByUserCard(userId, cardPk);
        if (model != null) {
            if (count == model.getCount()){
                throw new OperationException("[%s]的卡片[%s]的数量没有变化！", userName, cardName);
            }
            log.warn("修改[{}]的卡片数量（{}->{}）", userName, model.getCount(), count);
            model.setCount(count);
            clearCardListCache();
            return userCardListMapper.update(model) > 0;
        } else {
            if (count == 0){
                throw new OperationException("[%s]的卡片[%s]的数量没有变化！", userName, cardName);
            }
            log.warn("修改[{}]的卡片数量（{}->{}）", userName, 0, count);
            model = new UserCardListModel();
            model.setUserId(userId);
            model.setCardPk(cardPk);
            model.setCount(count);
            clearCardListCache();
            return userCardListMapper.insert(model) > 0;
        }
    }

    /**
     * 管理端根据条件筛选所有卡片
     *
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param rare        稀有度
     * @return 查询结果
     */
    @Override
    public List<CardListDTO> selectListAdmin(String packageName, String cardName, String rare) {
        return userCardListMapper.selectCardList(packageName, cardName, rare);
    }

    /**
     * 卡片列表缓存，避免频繁联表查询
     */
    static Map<String, List<CardOwnListDTO>> cardListCache = new HashMap<>();

    /**
     * 更新数据后，手动清除卡片列表缓存
     */
    static void clearCardListCache(){
        log.debug("卡片列表数据缓存被清除");
        cardListCache.clear();
    }

    @Override
    public List<CardOwnListDTO> selectList(String packageName, String cardName, String rare, String userName) {
        String key = String.format("{%s,%s,%s,%s}", packageName, cardName, rare, userName);
        log.debug("查询卡片列表：{}", key);
        List<CardOwnListDTO> cache = cardListCache.get(key);
        if (cache == null) {
            cache = userCardListMapper.selectCardOwnList(packageName, cardName, rare, userName);
            cardListCache.put(key, cache);
        }
        return cache;
    }
}
