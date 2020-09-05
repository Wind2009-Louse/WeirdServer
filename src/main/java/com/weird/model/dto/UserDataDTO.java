package com.weird.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户信息
 *
 * @author Nidhogg
 * @date   2020/09/04
 */
@Data
public class UserDataDTO implements Serializable {
    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 未出货的计数
     */
    private Integer nonawardCount;

    /**
     * 尘数
     */
    private Integer dustCount;
}