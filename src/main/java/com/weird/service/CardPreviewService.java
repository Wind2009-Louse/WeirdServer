package com.weird.service;

import com.weird.model.CardPreviewModel;

import java.util.List;

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

    /**
     * 根据卡片ID返回描述
     *
     * @param code 卡号
     * @return 卡片描述
     */
    CardPreviewModel selectPreviewByCode(long code);

    /**
     * 根据关键词从卡名和效果中查找符合条件的卡片
     *
     * @param  word 关键词
     * @return 卡名列表
     */
    List<String> blurSearch(String word);
}
