package com.weird.mapper.card;

import com.weird.model.CardDetailModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 卡片描述查询Mapper
 *
 * @author Nidhogg
 * @date 2020.9.25
 */
public interface CardDetailMapper {
    /**
     * 根据卡名查找卡片效果
     *
     * @param name 卡名
     * @return 效果Model
     */
    List<CardDetailModel> getDetailByName(@Param("name") String name);
}
