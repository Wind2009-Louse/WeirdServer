package com.weird.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户持有的卡片
 *
 * @author Nidhogg
 * @date   2020/09/04
 */
@Data
public class UserCardListModel implements Serializable {
    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 卡片PK
     */
    private Integer cardPk;

    /**
     * 持有数量
     */
    private Integer count;
}