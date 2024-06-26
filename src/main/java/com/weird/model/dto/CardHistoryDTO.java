package com.weird.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 卡片更改纪录DTO
 *
 * @author Nidhogg
 * @date 2020.9.12
 */
@Data
public class CardHistoryDTO implements Serializable {
    /**
     * 卡包名
     */
    String packageName;

    /**
     * 稀有度
     */
    String rare;

    long cardPk;

    /**
     * 卡片原名
     */
    String oldName;

    /**
     * 旧卡图ID
     */
    int oldPicId;

    /**
     * 旧描述
     */
    String oldDesc;

    /**
     * 新卡图ID
     */
    int newPicId;

    /**
     * 卡片现名
     */
    String newName;

    /**
     * 新描述
     */
    String newDesc;

    /**
     * 更改日期
     */
    Date createdTime;
}
