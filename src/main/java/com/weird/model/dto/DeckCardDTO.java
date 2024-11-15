package com.weird.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 卡组的卡片信息
 *
 * @author Nidhogg
 * @date 2021.8.14
 */
@Data
public class DeckCardDTO implements Serializable {
    /**
     * 卡片密码
     */
    long code;

    /**
     * 卡名
     */
    String cardName;

    /**
     * 卡片描述
     */
    String desc;

    /**
     * 数量
     */
    int count;

    /**
     * 持有数量
     */
    int own;

    /**
     * 卡包名
     */
    String packageName;

    /**
     * 稀有度
     */
    String rare;

    /**
     * 是否合法
     */
    boolean legal;

    public DeckCardDTO() {
    }

    public DeckCardDTO(long code, int count) {
        this.code = code;
        this.count = count;
    }
}
