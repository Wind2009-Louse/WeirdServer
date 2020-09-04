package com.weird.service.impl;

import com.weird.mapper.PackageInfoMapper;
import com.weird.model.PackageInfoModel;
import com.weird.service.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PackageServiceImpl implements PackageService {
    @Autowired
    PackageInfoMapper packageInfoMapper;

    /**
     * 添加卡包
     *
     * @param name 卡包名称
     * @return 是否添加成功
     */
    @Override
    public boolean addPackage(String name) {
        // 查找是否重名
        PackageInfoModel oldPackage = packageInfoMapper.selectByName(name);
        if (oldPackage != null){
            return false;
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
    public boolean updatePackageName(String oldName, String newName) {
        PackageInfoModel oldPackage = packageInfoMapper.selectByName(oldName);
        if (oldPackage == null){
            return false;
        }
        oldPackage.setPackageName(newName);
        return packageInfoMapper.updateByPrimaryKey(oldPackage) > 0;
    }
}
