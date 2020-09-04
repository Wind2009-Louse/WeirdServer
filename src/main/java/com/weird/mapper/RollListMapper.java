package com.weird.mapper;

import com.weird.model.RollListModel;

public interface RollListMapper {
    int deleteByPrimaryKey(Integer rollId);

    int insert(RollListModel record);

    int insertSelective(RollListModel record);

    RollListModel selectByPrimaryKey(Integer rollId);

    int updateByPrimaryKeySelective(RollListModel record);

    int updateByPrimaryKey(RollListModel record);
}