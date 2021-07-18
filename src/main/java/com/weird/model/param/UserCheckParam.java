package com.weird.model.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 检查用户登录
 *
 * @author Nidhogg
 * @date 2021.7.17
 */
@Data
public class UserCheckParam implements Serializable {
    /**
     * 操作用户名称
     */
    String name;

    /**
     * 操作用户密码
     */
    String password;
}
