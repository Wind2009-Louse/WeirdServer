package com.weird.mapper;

import com.weird.model.UserDataModel;

public interface UserDataMapper {
    int deleteByPrimaryKey(Integer userId);

    int insert(UserDataModel record);

    int insertSelective(UserDataModel record);

    UserDataModel selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(UserDataModel record);

    int updateByPrimaryKey(UserDataModel record);
}