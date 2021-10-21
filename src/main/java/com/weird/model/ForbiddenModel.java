package com.weird.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 禁限设置
 *
 * @author Nidhogg
 * @date 2021.10.21
 */
@Data
public class ForbiddenModel implements Serializable {
    /**
     * 主键
     */
    int id;

    /**
     * 卡片密码
     */
    int code;

    /**
     * 卡名
     */
    String name;

    /**
     * 限制数量
     */
    int count;
}
