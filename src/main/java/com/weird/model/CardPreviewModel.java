package com.weird.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 卡片详细效果
 *
 * @author Nidhogg
 * @date 2020.9.25
 */
@Data
public class CardPreviewModel implements Serializable {
    /**
     * 卡片ID
     */
    int id;

    /**
     * 卡片名称
     */
    String name;

    /**
     * 类型
     */
    int type;

    /**
     * ATK
     */
    int atk;

    /**
     * DEF
     */
    int def;

    /**
     * 等阶
     */
    int level;

    /**
     * 种族
     */
    int race;

    /**
     * 属性
     */
    int attribute;

    /**
     * 卡片效果
     */
    String desc;
}
