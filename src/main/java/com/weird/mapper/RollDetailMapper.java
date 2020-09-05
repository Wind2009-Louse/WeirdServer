package com.weird.mapper;

import com.weird.model.RollDetailModel;

import java.util.List;

public interface RollDetailMapper {
    int insert(RollDetailModel record);

    int insertSelective(RollDetailModel record);

    /**
     * 根据抽卡结果ID返回抽卡内容
     *
     * @param resultId
     * @return
     */
    List<Integer> selectCardPkById(long resultId);
}