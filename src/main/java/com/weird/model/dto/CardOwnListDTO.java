package com.weird.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 显示所有卡片拥有情况的列表
 *
 * @author Nidhogg
 */
@Data
public class CardOwnListDTO implements Serializable {
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
     * 用户名
     */
    String userName;

    /**
     * 持有数
     */
    int count;

    /**
     * 需要的硬币
     */
    int needCoin;
}
