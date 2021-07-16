package com.weird.service.impl;

import com.weird.mapper.card.CardPreviewMapper;
import com.weird.model.CardPreviewModel;
import com.weird.model.enums.CardAttributeEnum;
import com.weird.model.enums.CardRaceEnum;
import com.weird.model.enums.CardTypeEnum;
import com.weird.service.CardPreviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 卡片详细Service
 *
 * @author Nidhogg
 * @date 2020.9.25
 */
@Service
@Slf4j
public class CardPreviewServiceImpl implements CardPreviewService {
    @Autowired
    CardPreviewMapper cardPreviewMapper;

    static String[] cardTypeFilter = {"t:", "type:", "t=", "type="};
    static String[] cardAttributeFilter = {"a:", "attribute:", "a=", "attribute="};
    static String[] cardRaceFilter = {"r:", "race:", "r=", "race="};
    static String[] cardLevelFilter = {"l:", "lv:", "level:", "l=", "lv=", "level="};
    static String[] cardAttackFilter = {"atk:", "attack:", "atk=", "attack="};
    static String[] cardDefenseFilter = {"def:", "defense:", "def=", "defense="};
    static String[] cardScaleFilter = {"ls:", "scale:", "ls=", "scale="};

    /**
     * 根据卡名返回卡片详情
     *
     * @param name 卡名
     * @return 卡片描述
     */
    @Override
    public CardPreviewModel selectPreviewByName(String name) {
        try {
            List<CardPreviewModel> list = cardPreviewMapper.getPreviewByName(name);
            if (CollectionUtils.isEmpty(list)) {
                return null;
            } else {
                return list.get(0);
            }
        } catch (Exception e) {
            log.error("查询卡片[{}]效果时出现错误：{}", name, e.getMessage());
            return null;
        }
    }

    /**
     * 根据关键词从卡名和效果中查找符合条件的卡片
     *
     * @param word 关键词
     * @return 卡名列表
     */
    @Override
    public List<String> blurSearch(String word) {
        if (StringUtils.isEmpty(word)) {
            return null;
        }
        List<String> cardDescList = new LinkedList<>();
        Set<CardTypeEnum> cardTypeSet = new HashSet<>();
        Set<CardAttributeEnum> cardAttributeSet = new HashSet<>();
        Set<CardRaceEnum> cardRaceSet = new HashSet<>();
        Long cardLevel = null;
        Long cardAttack = null;
        Long cardDefense = null;
        Long cardScale = null;

        for (String rawText : word.split("\\|")) {
            boolean filtered = false;
            String realText = rawText.trim();
            if (StringUtils.isEmpty(realText)) {
                continue;
            }

            for (String filter : cardTypeFilter) {
                if (realText.startsWith(filter)) {
                    String filteredText = realText.substring(filter.length());
                    CardTypeEnum cardType = CardTypeEnum.getByName(filteredText);
                    if (cardType != null) {
                        cardTypeSet.add(cardType);
                        filtered = true;
                    }
                }
            }

            for (String filter : cardAttributeFilter) {
                if (realText.startsWith(filter)) {
                    String filteredText = realText.substring(filter.length());
                    CardAttributeEnum cardAttribute = CardAttributeEnum.getByName(filteredText);
                    if (cardAttribute != null) {
                        cardAttributeSet.add(cardAttribute);
                        filtered = true;
                    }
                }
            }

            for (String filter : cardRaceFilter) {
                if (realText.startsWith(filter)) {
                    String filteredText = realText.substring(filter.length());
                    CardRaceEnum cardRace = CardRaceEnum.getByName(filteredText);
                    if (cardRace != null) {
                        cardRaceSet.add(cardRace);
                        filtered = true;
                    }
                }
            }

            for (String filter : cardLevelFilter) {
                if (realText.startsWith(filter)) {
                    String filteredText = realText.substring(filter.length());
                    try {
                        cardLevel = Long.parseLong(filteredText);
                        filtered = true;
                        break;
                    } catch (Exception e) {
                        cardLevel = null;
                    }
                }
            }

            for (String filter : cardAttackFilter) {
                if (realText.startsWith(filter)) {
                    String filteredText = realText.substring(filter.length());
                    try {
                        cardAttack = Long.parseLong(filteredText);
                        filtered = true;
                        break;
                    } catch (Exception e) {
                        cardAttack = null;
                    }
                }
            }

            for (String filter : cardDefenseFilter) {
                if (realText.startsWith(filter)) {
                    String filteredText = realText.substring(filter.length());
                    try {
                        cardDefense = Long.parseLong(filteredText);
                        filtered = true;
                        break;
                    } catch (Exception e) {
                        cardDefense = null;
                    }
                }
            }

            for (String filter : cardScaleFilter) {
                if (realText.startsWith(filter)) {
                    String filteredText = realText.substring(filter.length());
                    try {
                        cardScale = Long.parseLong(filteredText);
                        filtered = true;
                        break;
                    } catch (Exception e) {
                        cardScale = null;
                    }
                }
            }

            if (!filtered) {
                cardDescList.add(realText);
            }
        }

        List<CardTypeEnum> cardTypeList = new LinkedList<>(cardTypeSet);
        List<CardAttributeEnum> cardAttributeList = new LinkedList<>(cardAttributeSet);
        List<CardRaceEnum> cardRaceList = new LinkedList<>(cardRaceSet);

        // TODO 刻度搜索
        return cardPreviewMapper.multiBlurSearch(
                cardDescList,
                cardTypeList,
                cardAttributeList,
                cardRaceList,
                cardLevel,
                cardAttack,
                cardDefense,
                cardScale);
    }
}
