package com.weird.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 抽卡结果的显示
 */
@Data
public class RollDetailDTO implements Serializable {
    /**
     * 卡片名
     */
    String cardName;

    /**
     * 卡图ID
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
}
