package com.weird.service.impl;

import com.weird.mapper.CardHistoryMapper;
import com.weird.mapper.PackageCardMapper;
import com.weird.mapper.PackageInfoMapper;
import com.weird.model.CardHistoryModel;
import com.weird.model.PackageCardModel;
import com.weird.model.PackageInfoModel;
import com.weird.service.PackageService;
import com.weird.utils.OperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.weird.service.impl.CardServiceImpl.clearCardListCache;
import static com.weird.service.impl.RollServiceImpl.clearRollListCache;

@Service
@Slf4j
public class PackageServiceImpl implements PackageService {
    @Autowired
    PackageInfoMapper packageInfoMapper;

    @Autowired
    PackageCardMapper packageCardMapper;

    @Autowired
    CardHistoryMapper cardHistoryMapper;

    /**
     * 根据名称查找卡包列表
     *
     * @param packageName 卡包名
     * @return 卡包列表
     */
    @Override
    public List<PackageInfoModel> selectByName(String packageName) throws Exception {
        return packageInfoMapper.selectByName(packageName);
    }

    /**
     * 添加卡包
     *
     * @param name 卡包名称
     * @return 是否添加成功
     */
    @Override
    public boolean addPackage(String name) throws Exception {
        // 查找是否重名
        PackageInfoModel oldPackage = packageInfoMapper.selectByNameDistinct(name);
        if (oldPackage != null) {
            throw new OperationException("卡包[%s]已存在！", name);
        }
        PackageInfoModel newPackage = new PackageInfoModel();
        newPackage.setPackageName(name);
        log.warn("添加卡包：[{}]", name);
        return packageInfoMapper.insert(newPackage) > 0;
    }

    /**
     * 更新卡包名称
     *
     * @param oldName 旧卡包名称
     * @param newName 新卡包名称
     * @return 是否更新成功
     */
    @Override
    public boolean updatePackageName(String oldName, String newName) throws Exception {
        PackageInfoModel oldPackage = packageInfoMapper.selectByNameDistinct(oldName);
        if (oldPackage == null) {
            throw new OperationException("找不到该卡包：[%s]！", oldName);
        }
        PackageInfoModel newPackage = packageInfoMapper.selectByNameDistinct(newName);
        if (newPackage != null){
            throw new OperationException("[%s]已存在！", newName);
        }

        oldPackage.setPackageName(newName);
        log.warn("卡包更改名字：[{}]->[{}]", oldName, newName);
        clearCardListCache();
        clearRollListCache();
        return packageInfoMapper.updateByPrimaryKey(oldPackage) > 0;
    }

    /**
     * 在卡包中添加卡片
     *
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param rare        稀有度
     * @return 是否添加成功
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean addCard(String packageName, String cardName, String rare) throws Exception {
        // 查找卡包是否存在
        PackageInfoModel packageModel = packageInfoMapper.selectByNameDistinct(packageName);
        if (packageModel == null) {
            throw new OperationException("找不到该卡包：[%s]！", packageName);
        }
        int packageId = packageModel.getPackageId();

        // 查找卡片是否存在
        PackageCardModel cardModel = packageCardMapper.selectInPackageDistinct(packageId, cardName);
        if (cardModel != null) {
            throw new OperationException("卡片[%s]已存在！", cardName);
        }

        // 添加
        PackageCardModel newCardModel = new PackageCardModel();
        newCardModel.setCardName(cardName);
        newCardModel.setPackageId(packageId);
        newCardModel.setRare(rare);
        log.warn("在卡包[{}]中添加卡片[{}]({})", packageName, cardName, rare);
        return packageCardMapper.insert(newCardModel) > 0;
    }

    /**
     * 修改卡包中的卡片名字
     *
     * @param packageName 卡包名
     * @param oldName     旧卡名
     * @param newName     新卡名
     * @param isShow      是否在历史记录中显示该卡片
     * @return 是否修改成功
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean updateCardName(String packageName,
                                  String oldName,
                                  String newName,
                                  int isShow) throws Exception {
        // 查找卡包是否存在
        PackageInfoModel packageModel = packageInfoMapper.selectByNameDistinct(packageName);
        if (packageModel == null) {
            throw new OperationException("找不到该卡包：[%s]！", packageName);
        }
        int packageId = packageModel.getPackageId();

        // 查找是否能否更新
        PackageCardModel cardModel = packageCardMapper.selectInPackageDistinct(packageId, oldName);
        if (cardModel == null) {
            throw new OperationException("卡片[%s]不存在！", oldName);
        }
        PackageCardModel goalModel = packageCardMapper.selectInPackageDistinct(packageId, newName);
        if (goalModel != null){
            throw new OperationException("卡片[%s]已存在！", newName);
        }

        // 修改
        log.warn("卡包[{}]中的卡片[{}]更改为[{}]", packageName, cardModel.getCardName(), newName);
        cardModel.setCardName(newName);
        int result = packageCardMapper.updateByPrimaryKey(cardModel);
        if (result > 0){
            if (isShow != 0){
                CardHistoryModel cardHistory = new CardHistoryModel();
                cardHistory.setPackageId(packageId);
                cardHistory.setCardPk(cardModel.getCardPk());
                cardHistory.setOldName(oldName);
                cardHistory.setNewName(newName);
                cardHistory.setRare(cardModel.getRare());
                cardHistoryMapper.insert(cardHistory);
            }

            clearCardListCache();
            clearRollListCache();
            return true;
        }
        return false;
    }
}
