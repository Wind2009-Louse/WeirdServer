package com.weird.mapper;

import com.weird.model.CardHistoryModel;
import com.weird.model.dto.CardHistoryDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CardHistoryMapper {
    /**
     * 插入纪录
     *
     * @param model 纪录
     * @return 插入数量
     */
    int insert(CardHistoryModel model);

    /**
     * 根据参数查找修改过的卡片Pk
     *
     * @param packageIndexList 卡包编号ID列表
     * @param cardName 卡片名
     * @param rare 稀有度
     * @return
     */
    List<Integer> selectCardPk(
            @Param("packages") List<Integer> packageIndexList,
            @Param("card") String cardName,
            @Param("rare") String rare
    );

    /**
     * 根据卡片Pk查找修改纪录
     *
     * @param cardPks 卡片Pk
     * @return 修改纪录
     */
    List<CardHistoryDTO> selectByCardPk(@Param("cards") List<Integer> cardPks);
}