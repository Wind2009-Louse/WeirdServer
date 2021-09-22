package com.weird.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 转盘结果
 *
 * @author Nidhogg
 * @date 2021.9.22
 */
@Data
public class RouletteResultDTO implements Serializable {
    /**
     * 转盘指针
     */
    int index;

    /**
     * 返回信息
     */
    String result;
}
