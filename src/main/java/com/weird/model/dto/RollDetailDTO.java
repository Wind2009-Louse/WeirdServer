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
     * 稀有度
     */
    String rare;
}
