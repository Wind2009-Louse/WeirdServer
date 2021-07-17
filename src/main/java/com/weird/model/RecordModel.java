package com.weird.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 记录
 *
 * @author Nidhogg
 * @date 2021.7.17
 */
@Data
public class RecordModel implements Serializable {
    /**
     * 记录ID
     */
    long recordId;

    /**
     * 操作人
     */
    String operator;

    /**
     * 操作时间
     */
    long time;

    /**
     * 操作内容
     */
    String text;
}
