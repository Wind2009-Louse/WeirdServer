package com.weird.service;

import com.weird.model.CardDetailModel;

/**
 * 卡片详细Service
 *
 * @author Nidhogg
 * @date 2020.9.25
 */
public interface CardDetailService {
    /**
     * 根据卡名返回描述
     *
     * @param name 卡名
     * @return 卡片描述
     */
    CardDetailModel selectDetailsByName(String name);
}
