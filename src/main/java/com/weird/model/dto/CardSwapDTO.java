package com.weird.model.dto;

import com.weird.model.Trimable;
import lombok.Data;

import java.io.Serializable;

/**
 * 卡片交换DTO
 *
 * @author Nidhogg
 * @date 2020.10.7
 */
@Data
public class CardSwapDTO implements Serializable, Trimable {
    /**
     * 用户名
     */
    String name;

    /**
     * 密码
     */
    String password;

    /**
     * 交换的用户A
     */
    String userA;

    /**
     * 交换的用户B
     */
    String userB;

    /**
     * 用户A用以交换的卡A
     */
    String cardA;

    /**
     * 用户B用以交换的卡B
     */
    String cardB;
}