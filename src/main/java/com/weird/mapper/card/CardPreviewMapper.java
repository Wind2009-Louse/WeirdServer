package com.weird.mapper.card;

import com.weird.model.CardPreviewModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 卡片描述查询Mapper
 *
 * @author Nidhogg
 * @date 2020.9.25
 */
public interface CardPreviewMapper {
    /**
     * 根据卡名查找卡片效果
     *
     * @param name 卡名
     * @return 效果Model
     */
    List<CardPreviewModel> getPreviewByName(@Param("name") String name);
}
