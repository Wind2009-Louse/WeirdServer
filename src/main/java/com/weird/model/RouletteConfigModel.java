package com.weird.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 转盘配置
 *
 * @author Nidhogg
 * @date 2021.09.22
 */
@Data
public class RouletteConfigModel implements Serializable {
    /**
     * 参数ID
     */
    long configId;

    /**
     * 配置项说明
     */
    String detail;

    /**
     * 配置项比例
     */
    int rate;

    /**
     * 配置项背景色
     */
    String color;
}
