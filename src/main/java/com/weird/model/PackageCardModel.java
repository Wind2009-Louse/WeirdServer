package com.weird.model;

import lombok.Data;

/**
 * 卡包中的卡片信息
 *
 * @author Nidhogg
 * @date   2020/09/04
 */
@Data
public class PackageCardModel {
    /**
     * 卡片主键
     */
    private int cardPk;

    /**
     * 卡密
     */
    private long cardId;

    /**
     * 卡名
     */
    private String cardName;

    /**
     * 所在卡包
     */
    private int packageId;

    /**
     * 稀有度
     */
    private String rare;
}