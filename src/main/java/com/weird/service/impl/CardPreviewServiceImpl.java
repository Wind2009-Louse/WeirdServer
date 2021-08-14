package com.weird.service.impl;

import com.weird.mapper.card.CardPreviewMapper;
import com.weird.model.CardPreviewModel;
import com.weird.model.enums.CardAttributeEnum;
import com.weird.model.enums.CardRaceEnum;
import com.weird.model.enums.CardTypeEnum;
import com.weird.model.param.BlurSearchParam;
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

    static String[] cardTypeFilter = {"t:", "type:", "t：", "type：", "类型:", "类型："};
    static String[] cardAttributeFilter = {"a:", "attribute:", "a：", "attribute：", "属性:", "属性："};
    static String[] cardRaceFilter = {"r:", "race:", "r：", "race：", "种族:", "种族："};
    static String[] cardLevelFilter = {"l:", "lv:", "level:", "l：", "lv：", "level：", "等级:", "等级："};
    static String[] cardAttackFilter = {"atk:", "attack:", "atk：", "attack：", "攻击力:", "攻击力：", "攻击:", "攻击："};
    static String[] cardDefenseFilter = {"def:", "defense:", "def：", "defense：", "守备力:", "守备力：", "守备:", "守备："};
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
        BlurSearchParam param = new BlurSearchParam();

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
                        param.getCardTypeSet().add(cardType);
                        filtered = true;
                    }
                }
            }

            for (String filter : cardAttributeFilter) {
                if (realText.startsWith(filter)) {
                    String filteredText = realText.substring(filter.length());
                    CardAttributeEnum cardAttribute = CardAttributeEnum.getByName(filteredText);
                    if (cardAttribute != null) {
                        param.getCardAttributeSet().add(cardAttribute);
                        filtered = true;
                    }
                }
            }

            for (String filter : cardRaceFilter) {
                if (realText.startsWith(filter)) {
                    String filteredText = realText.substring(filter.length());
                    CardRaceEnum cardRace = CardRaceEnum.getByName(filteredText);
                    if (cardRace != null) {
                        param.getCardRaceSet().add(cardRace);
                        filtered = true;
                    }
                }
            }

            for (String filter : cardLevelFilter) {
                if (realText.startsWith(filter)) {
                    String filteredText = realText.substring(filter.length());
                    try {
                        param.setCardLevel(Long.parseLong(filteredText));
                        filtered = true;
                        break;
                    } catch (Exception e) {
                        param.setCardLevel(null);
                    }
                }
            }

            for (String filter : cardAttackFilter) {
                if (realText.startsWith(filter)) {
                    String filteredText = realText.substring(filter.length());
                    if ("?".equals(filteredText)) {
                        param.setCardAttack(-2L);
                        filtered = true;
                        break;
                    } else {
                        try {
                            param.setCardAttack(Long.parseLong(filteredText));
                            filtered = true;
                            break;
                        } catch (Exception e) {
                            param.setCardAttack(null);
                        }
                    }
                }
            }

            for (String filter : cardDefenseFilter) {
                if (realText.startsWith(filter)) {
                    String filteredText = realText.substring(filter.length());
                    if ("?".equals(filteredText)) {
                        param.setCardDefense(-2L);
                        filtered = true;
                        break;
                    } else {
                        try {
                            param.setCardDefense(Long.parseLong(filteredText));
                            filtered = true;
                            break;
                        } catch (Exception e) {
                            param.setCardDefense(null);
                        }
                    }
                }
            }

            for (String filter : cardScaleFilter) {
                if (realText.startsWith(filter)) {
                    String filteredText = realText.substring(filter.length());
                    if ("?".equals(filteredText)) {
                        param.setCardScale(-2L);
                        filtered = true;
                        break;
                    } else {
                        try {
                            param.setCardScale(Long.parseLong(filteredText));
                            filtered = true;
                            break;
                        } catch (Exception e) {
                            param.setCardScale(null);
                        }
                    }
                }
            }

            if (!filtered) {
                param.getCardDescList().add(realText);
            }
        }

        param.build();

        // TODO 刻度搜索
        return cardPreviewMapper.multiBlurSearch(param);
    }
}
