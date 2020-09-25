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
     * 描述
     */
    String desc;

    /**
     * 稀有度
     */
    String rare;
}
