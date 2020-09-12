package com.weird.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 卡片更改纪录
 *
 * @author Nidhogg
 * @date 2020.9.12
 */
@Data
public class CardHistoryModel implements Serializable {
    /**
     * 卡包ID
     */
    int packageId;

    /**
     * 卡片PK
     */
    int cardPk;

    /**
     * 稀有度
     */
    String rare;

    /**
     * 卡片原名
     */
    String oldName;

    /**
     * 卡片现名
     */
    String newName;

    /**
     * 更改日期
     */
    Date createdTime;
}
