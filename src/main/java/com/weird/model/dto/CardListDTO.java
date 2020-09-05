package com.weird.model.dto;

import lombok.Data;

/**
 * 显示所有卡片（管理端）
 *
 * @author Nidhogg
 */
@Data
public class CardListDTO {
    /**
     * 卡包名
     */
    String packageName;

    /**
     * 卡片名
     */
    String cardName;

    /**
     * 稀有度
     */
    String rare;
}
