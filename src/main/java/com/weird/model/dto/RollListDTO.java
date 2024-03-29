package com.weird.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 抽卡记录DTO
 *
 * @author Nidhogg
 */
@Data
public class RollListDTO implements Serializable {
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
    private List<RollDetailDTO> rollResult;

    /**
     * 是否被禁用（滚回）
     */
    private Byte isDisabled;

    /**
     * 抽卡时间
     */
    private Date time;
}