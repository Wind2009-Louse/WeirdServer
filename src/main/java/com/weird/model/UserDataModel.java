package com.weird.model;

import lombok.Data;

/**
 * 用户信息
 *
 * @author Nidhogg
 * @date   2020/09/04
 */
@Data
public class UserDataModel {
    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户密码
     */
    private String password;
}