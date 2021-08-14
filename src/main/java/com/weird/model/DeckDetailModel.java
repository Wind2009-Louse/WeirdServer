package com.weird.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 卡组卡片类
 *
 * @author Nidnogg
 * @date 2021.8.14
 */
@Data
public class DeckDetailModel implements Serializable {
    /**
     * 卡片ID
     */
    int detailId;

    /**
     * 卡组ID
     */
    int deckId;

    /**
     * 卡片密码
     */
    long code;

    /**
     * 数量
     */
    int count;

    /**
     * 类型（1=主卡组，2=额外，3=side）
     */
    int type;
}
