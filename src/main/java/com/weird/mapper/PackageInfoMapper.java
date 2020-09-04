package com.weird.mapper;

import com.weird.model.PackageInfoModel;

public interface PackageInfoMapper {
    int deleteByPrimaryKey(Integer packageId);

    int insert(PackageInfoModel record);

    int insertSelective(PackageInfoModel record);

    PackageInfoModel selectByPrimaryKey(Integer packageId);

    int updateByPrimaryKeySelective(PackageInfoModel record);

    int updateByPrimaryKey(PackageInfoModel record);
}