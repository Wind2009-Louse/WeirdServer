package com.weird.mapper;

import com.weird.model.CardHistoryModel;

public interface CardHistoryMapper {
    /**
     * 插入纪录
     *
     * @param model 纪录
     * @return 插入数量
     */
    int insert(CardHistoryModel model);
}
