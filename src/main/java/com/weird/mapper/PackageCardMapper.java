package com.weird.mapper;

import com.weird.model.PackageCardModel;

public interface PackageCardMapper {
    int deleteByPrimaryKey(Long pk);

    int insert(PackageCardModel record);

    int insertSelective(PackageCardModel record);

    PackageCardModel selectByPrimaryKey(Long pk);

    int updateByPrimaryKeySelective(PackageCardModel record);

    int updateByPrimaryKey(PackageCardModel record);
}