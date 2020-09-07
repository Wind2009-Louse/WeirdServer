package com.weird.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 卡片持有信息DTO
 *
 * @author Nidhogg
 */
@Data
public class PackageCardDTO implements Serializable {
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
    private String packageName;

    /**
     * 稀有度
     */
    private String rare;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户持有数
     */
    private int count;
}
