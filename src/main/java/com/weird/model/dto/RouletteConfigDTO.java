package com.weird.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 转盘配置，见{@link com.weird.model.RouletteConfigModel}
 *
 * @author Nidhogg
 * @date 2021.09.22
 */
@Data
public class RouletteConfigDTO implements Serializable {
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
