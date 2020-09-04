package com.weird.mapper;

import com.weird.model.RollInfoModel;

public interface RollInfoMapper {
    int insert(RollInfoModel record);

    int insertSelective(RollInfoModel record);
}