package com.weird.service.impl;

import com.weird.mapper.PackageCardMapper;
import com.weird.mapper.PackageInfoMapper;
import com.weird.model.PackageCardModel;
import com.weird.model.PackageInfoModel;
import com.weird.service.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PackageServiceImpl implements PackageService {
    @Autowired
    PackageInfoMapper packageInfoMapper;

    @Autowired
    PackageCardMapper packageCardMapper;

    /**
     * 添加卡包
     *
     * @param name 卡包名称
     * @return 是否添加成功
     */
    @Override
    public boolean addPackage(String name) throws Exception {
        // 查找是否重名
        PackageInfoModel oldPackage = packageInfoMapper.selectByName(name);
        if (oldPackage != null){
            throw new Exception("卡包已存在！");
        }
        PackageInfoModel newPackage = new PackageInfoModel();
        newPackage.setPackageName(name);
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
        PackageInfoModel oldPackage = packageInfoMapper.selectByName(oldName);
        if (oldPackage == null){
            throw new Exception("找不到该卡包！");
        }
        if (oldPackage.getPackageName().equals(newName)){
            throw new Exception("卡包名字未改动！");
        }
        oldPackage.setPackageName(newName);
        return packageInfoMapper.updateByPrimaryKey(oldPackage) > 0;
    }

    /**
     * 在卡包中添加卡片
     *
     * @param packageName 卡包名
     * @param cardName 卡片名
     * @param rare 稀有度
     * @return 是否添加成功
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean addCard(String packageName, String cardName, String rare) throws Exception {
        // 查找卡包是否存在
        PackageInfoModel packageModel = packageInfoMapper.selectByName(packageName);
        if (packageModel == null){
            throw new Exception("找不到该卡包！");
        }
        int packageId = packageModel.getPackageId();

        // 查找卡片是否存在
        PackageCardModel cardModel = packageCardMapper.selectInPackageDistinct(packageId, cardName);
        if (cardModel != null){
            throw new Exception("卡片已存在！");
        }

        // 添加
        PackageCardModel newCardModel = new PackageCardModel();
        newCardModel.setCardName(cardName);
        newCardModel.setPackageId(packageId);
        newCardModel.setRare(rare);
        return packageCardMapper.insert(newCardModel) > 0;
    }

    /**
     * 修改卡包中的卡片名字
     *
     * @param packageName 卡包名
     * @param oldName 旧卡名
     * @param newName 新卡名
     * @return 是否修改成功
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean updateCardName(String packageName, String oldName, String newName) throws Exception {
        // 查找卡包是否存在
        PackageInfoModel packageModel = packageInfoMapper.selectByName(packageName);
        if (packageModel == null){
            throw new Exception("找不到该卡包！");
        }
        int packageId = packageModel.getPackageId();

        // 查找卡片是否存在
        PackageCardModel cardModel = packageCardMapper.selectInPackageDistinct(packageId, oldName);
        if (cardModel == null){
            throw new Exception("卡片不存在！");
        }

        // 修改
        cardModel.setCardName(newName);
        return packageCardMapper.updateByPrimaryKey(cardModel) > 0;
    }
}
