package com.weird.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 转盘历史记录
 *
 * @author Nidhogg
 * @date 2021.9.22
 */
@Data
public class RouletteHistoryDTO implements Serializable {
    /**
     * 用户名
     */
    String userName;

    /**
     * 详情
     */
    String detail;

    /**
     * 操作时间
     */
    Date createdTime;
}
