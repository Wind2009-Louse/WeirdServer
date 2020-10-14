package com.weird.service.impl;

import com.weird.mapper.card.CardPreviewMapper;
import com.weird.model.CardPreviewModel;
import com.weird.service.CardPreviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            if (list == null || list.size() <= 0) {
                return null;
            } else {
                return list.get(0);
            }
        } catch (Exception e) {
            return null;
        }
    }

}
