package com.weird.service.impl;

import com.weird.handler.CardSearchHandler;
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

    @Autowired
    List<CardSearchHandler> cardSearchHandlers;

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
     * 根据卡名返回卡片详情
     *
     * @param code 卡号
     * @return 卡片描述
     */
    @Override
    public CardPreviewModel selectPreviewByCode(long code) {
        try {
            List<CardPreviewModel> list = cardPreviewMapper.getPreviewByCode(code);
            if (CollectionUtils.isEmpty(list)) {
                return null;
            } else {
                return list.get(0);
            }
        } catch (Exception e) {
            log.error("查询卡片[{}]效果时出现错误：{}", code, e.getMessage());
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

        for (String rawText : word.split("[\\| ]")) {
            String realText = rawText.trim();
            if (StringUtils.isEmpty(realText)) {
                continue;
            }
            boolean handled = false;

            for (CardSearchHandler handler : cardSearchHandlers) {
                handled |= handler.handleParam(realText, param);
            }

            if (!handled) {
                param.getCardDescList().add(realText);
            }
        }
        param.build();

        return cardPreviewMapper.multiBlurSearch(param);
    }
}
