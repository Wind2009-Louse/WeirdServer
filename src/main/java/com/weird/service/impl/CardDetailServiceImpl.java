package com.weird.service.impl;

import com.weird.mapper.card.CardDetailMapper;
import com.weird.model.CardDetailModel;
import com.weird.service.CardDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 卡片详细Service
 *
 * @author Nidhogg
 * @date 2020.9.25
 */
@Service
@Slf4j
public class CardDetailServiceImpl implements CardDetailService {
    @Autowired
    CardDetailMapper cardDetailMapper;

    static public Map<String, String> cache = new HashMap<>();

    /**
     * 根据卡名返回卡片详情
     *
     * @param name 卡名
     * @return 卡片描述
     */
    @Override
    public CardDetailModel selectDetailsByName(String name) {
        log.info("查找[{}]的描述", name);
        List<CardDetailModel> list = cardDetailMapper.getDetailByName(name);
        if (list == null || list.size() <= 0) {
            return null;
        } else {
            return list.get(0);
        }
    }

}
