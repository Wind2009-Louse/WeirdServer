package com.weird.mapper;

import com.weird.model.PackageInfoModel;

public interface PackageInfoMapper {
    int deleteByPrimaryKey(Integer packageId);

    int insert(PackageInfoModel record);

    int insertSelective(PackageInfoModel record);

    PackageInfoModel selectByPrimaryKey(Integer packageId);

    int updateByPrimaryKeySelective(PackageInfoModel record);

    int updateByPrimaryKey(PackageInfoModel record);

    /**
     * 根据卡包名搜索卡包信息
     *
     * @param packageName 卡包名
     * @return 卡包信息
     */
    PackageInfoModel selectByName(String packageName);
}