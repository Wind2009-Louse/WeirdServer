package com.weird.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 抽卡结果的显示
 * @author Nidhogg
 */
@Data
public class RollDetailDTO implements Serializable {
    /**
     * 卡片名
     */
    String cardName;

    /**
     * 是否转化为尘
     */
    private Byte isDust;

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
