package com.weird.service.impl;

import com.weird.mapper.PackageCardMapper;
import com.weird.mapper.PackageInfoMapper;
import com.weird.mapper.UserCardListMapper;
import com.weird.mapper.UserDataMapper;
import com.weird.model.PackageCardModel;
import com.weird.model.PackageInfoModel;
import com.weird.model.UserCardListModel;
import com.weird.model.UserDataModel;
import com.weird.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
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
     * 根据用户名、卡包名、卡片名获得用户ID和卡片ID
     *
     * @param userName 用户名
     * @param packageName 卡包名
     * @param cardName 卡片名
     * @return 列表，其中[0]=用户ID，[1]=卡片ID
     */
    private List<Integer> getUserIdAndCardPk(String userName, String packageName, String cardName) throws Exception{
        UserDataModel userModel = userDataMapper.selectByNameDistinct(userName);
        if (userModel == null){
            throw new Exception("找不到该用户！");
        }

        PackageInfoModel packageModel = packageInfoMapper.selectByName(packageName);
        if (packageModel == null){
            throw new Exception("找不到该卡包！");
        }
        PackageCardModel cardModel = packageCardMapper.selectInPackageDistinct(packageModel.getPackageId(), cardName);
        if (cardModel == null){
            throw new Exception("找不到该卡片！");
        }

        return Arrays.asList(userModel.getUserId(), cardModel.getCardPk());
    }

    /**
     * 修改用户持有的卡片数量
     *
     * @param userName 用户名
     * @param packageName 卡包名
     * @param cardName 卡片名
     * @param count 新的卡片数量
     * @return 是否修改成功
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean updateCardCount(String userName, String packageName, String cardName, int count) throws Exception {
        if (count > 3 || count < 0){
            throw new Exception("修改卡片数量错误，应在0~3内！");
        }
        List<Integer> searchInfo = getUserIdAndCardPk(userName, packageName, cardName);
        int userId = searchInfo.get(0);
        int cardPk = searchInfo.get(1);

        UserCardListModel model = userCardListMapper.selectByUserCard(userId, cardPk);
        if (model != null){
            model.setCount(count);
            return userCardListMapper.update(model) > 0;
        } else {
            model = new UserCardListModel();
            model.setUserId(userId);
            model.setCardPk(cardPk);
            model.setCount(count);
            return userCardListMapper.insert(model) > 0;
        }
    }
}
