package com.weird.model.dto;

import com.weird.model.enums.LoginTypeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录情况
 *
 * @author Nidhogg
 */
@Data
public class UserSessionDTO implements Serializable {
    /**
     * 用户名
     */
    String userName;

    /**
     * 用户类型
     */
    LoginTypeEnum type;

    /**
     * 令牌
     */
    String session;

    /**
     * 到期时间戳
     */
    long expireTimestamp;

    public UserSessionDTO() {
        this.userName = "";
        this.type = LoginTypeEnum.UNLOGIN;
        this.session = "";
        this.expireTimestamp = 0;
    }
}
