package com.weird.model.param;

import com.weird.model.enums.CardAttributeEnum;
import com.weird.model.enums.CardRaceEnum;
import com.weird.model.enums.CardTypeEnum;
import lombok.Data;
import org.springframework.util.CollectionUtils;

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
     * 卡片名称
     */
    List<String> cardNameList;

    /**
     * 卡片等级
     */
    Long cardLevel;
    List<Long> cardLevelGe;
    List<Long> cardLevelG;
    List<Long> cardLevelLe;
    List<Long> cardLevelL;

    /**
     * 卡片攻击
     */
    Long cardAttack;
    List<Long> cardAttackGe;
    List<Long> cardAttackG;
    List<Long> cardAttackLe;
    List<Long> cardAttackL;

    /**
     * 卡片防御
     */
    Long cardDefense;
    List<Long> cardDefenseGe;
    List<Long> cardDefenseG;
    List<Long> cardDefenseLe;
    List<Long> cardDefenseL;

    /**
     * 卡片刻度
     */
    Long cardScale;
    List<Long> cardScaleGe;
    List<Long> cardScaleG;
    List<Long> cardScaleLe;
    List<Long> cardScaleL;

    List<CardTypeEnum> cardTypeList;
    List<CardAttributeEnum> cardAttributeList;
    List<CardRaceEnum> cardRaceList;

    public BlurSearchParam() {
        cardDescList = new LinkedList<>();
        cardTypeSet = new HashSet<>();
        cardAttributeSet = new HashSet<>();
        cardRaceSet = new HashSet<>();
        cardNameList = new LinkedList<>();

        cardLevel = null;
        cardLevelGe = new LinkedList<>();
        cardLevelG = new LinkedList<>();
        cardLevelLe = new LinkedList<>();
        cardLevelL = new LinkedList<>();

        cardAttack = null;
        cardAttackGe = new LinkedList<>();
        cardAttackG = new LinkedList<>();
        cardAttackLe = new LinkedList<>();
        cardAttackL = new LinkedList<>();

        cardDefense = null;
        cardDefenseGe = new LinkedList<>();
        cardDefenseG = new LinkedList<>();
        cardDefenseLe = new LinkedList<>();
        cardDefenseL = new LinkedList<>();

        cardScale = null;
        cardScaleGe = new LinkedList<>();
        cardScaleG = new LinkedList<>();
        cardScaleLe = new LinkedList<>();
        cardScaleL = new LinkedList<>();
    }

    public void build() {
        if (cardLevel != null
                || !CollectionUtils.isEmpty(cardLevelGe) || !CollectionUtils.isEmpty(cardLevelG) || !CollectionUtils.isEmpty(cardLevelLe) || !CollectionUtils.isEmpty(cardLevelL)
                || cardAttack != null
                || !CollectionUtils.isEmpty(cardAttackGe) || !CollectionUtils.isEmpty(cardAttackG) || !CollectionUtils.isEmpty(cardAttackLe) || !CollectionUtils.isEmpty(cardAttackL)
                || cardDefense != null
                || !CollectionUtils.isEmpty(cardDefenseGe) || !CollectionUtils.isEmpty(cardDefenseG) || !CollectionUtils.isEmpty(cardDefenseLe) || !CollectionUtils.isEmpty(cardDefenseL)
                || cardScale != null
        ) {
            cardTypeSet.add(CardTypeEnum.MONSTER);
        }

        cardTypeList = new LinkedList<>(cardTypeSet);
        cardAttributeList = new LinkedList<>(cardAttributeSet);
        cardRaceList = new LinkedList<>(cardRaceSet);
    }
}
