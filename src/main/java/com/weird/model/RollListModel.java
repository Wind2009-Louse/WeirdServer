package com.weird.model;

import lombok.Data;

/**
 * 抽卡列表
 *
 * @author Nidhogg
 * @date   2020/09/04
 */
@Data
public class RollListModel {
    /**
     * 抽卡记录ID
     */
    private Long rollId;

    /**
     * 抽卡用户ID
     */
    private Integer rollUserId;

    /**
     * 卡包ID
     */
    private Integer rollPackageId;

    /**
     * 是否被禁用（滚回）
     */
    private Byte isDisabled;

    /**
     * 抽卡时间
     */
    private long time;
}