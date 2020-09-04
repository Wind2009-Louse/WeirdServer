package com.weird.mapper;

import com.weird.model.UserCardListModel;

public interface UserCardListMapper {
    int insert(UserCardListModel record);

    int insertSelective(UserCardListModel record);
}