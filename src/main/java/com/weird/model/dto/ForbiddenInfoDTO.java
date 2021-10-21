package com.weird.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 禁限显示，见{@link com.weird.model.ForbiddenModel}
 *
 * @author Nidhogg
 * @date 2021.10.21
 */
@Data
public class ForbiddenInfoDTO implements Serializable {
    /**
     * 卡片密码
     */
    int code;

    /**
     * 卡名
     */
    String name;

    /**
     * 卡片说明
     */
    String detail;
}
