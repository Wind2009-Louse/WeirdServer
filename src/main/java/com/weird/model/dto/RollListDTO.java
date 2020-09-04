package com.weird.model.dto;

import lombok.Data;

/**
 * 抽卡记录DTO
 */
@Data
public class RollListDTO {
    /**
     * 抽卡记录ID
     */
    private Long rollId;

    /**
     * 抽卡用户名称
     */
    private String rollUserName;

    /**
     * 卡包名
     */
    private String rollPackageName;

    /**
     * 抽卡结果
     */
    private String rollResult;

    /**
     * 是否被禁用（滚回）
     */
    private Byte isDisabled;

    /**
     * 抽卡时间
     */
    private long time;
}