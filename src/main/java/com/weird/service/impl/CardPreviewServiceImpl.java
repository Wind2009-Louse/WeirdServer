package com.weird.service.impl;

import com.weird.mapper.card.CardPreviewMapper;
import com.weird.model.CardPreviewModel;
import com.weird.service.CardPreviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

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
            return Collections.emptyList();
        }
        return cardPreviewMapper.blurSearch(word);
    }
}
