package com.weird.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户信息
 *
 * @author Nidhogg
 * @date 2020/09/04
 */
@Data
public class UserDataModel implements Serializable {
    /**
     * 用户ID
     */
    private int userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 是否管理员
     */
    private Byte isAdmin;

    /**
     * 未出货的计数
     */
    private int nonawardCount;

    /**
     * 尘数
     */
    private int dustCount;

    /**
     * DP
     */
    private int duelPoint;
}