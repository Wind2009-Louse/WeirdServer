package com.weird.mapper.card;

import com.weird.model.CardPreviewModel;
import com.weird.model.enums.CardAttributeEnum;
import com.weird.model.enums.CardRaceEnum;
import com.weird.model.enums.CardTypeEnum;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

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

    /**
     * 根据关键词从卡名和效果中查找符合条件的卡片
     *
     * @param  word 关键词
     * @return 卡名列表
     */
    List<String> blurSearch(@Param("word") String word);

    /**
     * 多重条件查询
     *
     * @param wordList 卡名/效果列表
     * @param typeList 类型列表
     * @return
     */
    List<String> multiBlurSearch(@Param("wordList") List<String> wordList,
                                 @Param("typeList") List<CardTypeEnum> typeList,
                                 @Param("attributeList") List<CardAttributeEnum> attributeList,
                                 @Param("raceList") List<CardRaceEnum> raceList,
                                 @Param("level") Long level,
                                 @Param("attack") Long attack,
                                 @Param("defense") Long defense,
                                 @Param("scale") Long scale);
}
