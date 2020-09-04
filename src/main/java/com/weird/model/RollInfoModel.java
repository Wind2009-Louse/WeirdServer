package com.weird.model;

import lombok.Data;

/**
 * 抽卡详细记录
 *
 * @author Nidhogg
 * @date   2020/09/04
 */
@Data
public class RollInfoModel {
    /**
     * 抽卡记录ID
     */
    private Long rollId;

    /**
     * 抽卡PK
     */
    private Integer cardPk;

    /**
     * 是否转化为尘
     */
    private Byte isDust;
}