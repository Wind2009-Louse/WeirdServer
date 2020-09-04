package com.weird.mapper;

import com.weird.model.UserDustModel;

public interface UserDustMapper {
    int deleteByPrimaryKey(Integer userId);

    int insert(UserDustModel record);

    int insertSelective(UserDustModel record);

    UserDustModel selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(UserDustModel record);

    int updateByPrimaryKey(UserDustModel record);
}