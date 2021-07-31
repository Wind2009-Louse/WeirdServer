package com.weird.model.param;

import com.weird.model.enums.CardAttributeEnum;
import com.weird.model.enums.CardRaceEnum;
import com.weird.model.enums.CardTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 数据库中搜索卡片列表的参数
 *
 * @author Nidhogg
 * @date 2021.7.31
 */
@Data
public class BlurSearchParam implements Serializable {
    /**
     * 卡片描述
     */
    List<String> cardDescList;

    /**
     * 卡片种类
     */
    Set<CardTypeEnum> cardTypeSet;

    /**
     * 卡片属性
     */
    Set<CardAttributeEnum> cardAttributeSet;

    /**
     * 卡片种族
     */
    Set<CardRaceEnum> cardRaceSet;

    /**
     * 卡片等级
     */
    Long cardLevel;

    /**
     * 卡片攻击
     */
    Long cardAttack;

    /**
     * 卡片防御
     */
    Long cardDefense;
    Long cardScale;

    List<CardTypeEnum> cardTypeList;
    List<CardAttributeEnum> cardAttributeList;
    List<CardRaceEnum> cardRaceList;

    public BlurSearchParam() {
        cardDescList = new LinkedList<>();
        cardTypeSet = new HashSet<>();
        cardAttributeSet = new HashSet<>();
        cardRaceSet = new HashSet<>();
        cardLevel = null;
        cardAttack = null;
        cardDefense = null;
        cardScale = null;
    }

    public void build() {
        cardTypeList = new LinkedList<>(cardTypeSet);
        cardAttributeList = new LinkedList<>(cardAttributeSet);
        cardRaceList = new LinkedList<>(cardRaceSet);
    }
}
