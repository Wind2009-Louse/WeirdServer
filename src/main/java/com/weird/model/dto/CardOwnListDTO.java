package com.weird.model.dto;

import lombok.Data;

/**
 * 显示所有卡片拥有情况的列表
 *
 * @author Nidhogg
 */
@Data
public class CardOwnListDTO {
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

    /**
     * 用户名
     */
    String userName;

    /**
     * 持有数
     */
    int count;
}
