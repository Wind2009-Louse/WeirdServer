package com.weird.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 显示所有卡片（管理端）
 *
 * @author Nidhogg
 */
@Data
public class CardListDTO implements Serializable {
    /**
     * 卡包名
     */
    String packageName;

    /**
     * 卡片名
     */
    String cardName;

    /**
     * 卡图Id
     */
    int picId;

    /**
     * 描述
     */
    String desc;

    /**
     * 稀有度
     */
    String rare;

    /**
     * 持有数量
     */
    int count;

    /**
     * 是否在收藏中
     */
    int inCollection;
}
