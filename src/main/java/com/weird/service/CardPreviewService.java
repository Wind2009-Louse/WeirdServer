package com.weird.service;

import com.weird.model.CardPreviewModel;

/**
 * 卡片详细Service
 *
 * @author Nidhogg
 * @date 2020.9.25
 */
public interface CardPreviewService {
    /**
     * 根据卡名返回描述
     *
     * @param name 卡名
     * @return 卡片描述
     */
    CardPreviewModel selectPreviewByName(String name);
}
