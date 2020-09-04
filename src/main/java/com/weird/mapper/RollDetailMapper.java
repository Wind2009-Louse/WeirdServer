package com.weird.mapper;

import com.weird.model.RollDetailModel;

public interface RollDetailMapper {
    int insert(RollDetailModel record);

    int insertSelective(RollDetailModel record);
}