package com.weird.model.param;

import com.weird.interfaces.Trimable;
import lombok.Data;

import java.io.Serializable;

/**
 * 管理员替换卡片参数
 *
 * @author Nidhogg
 * @date 2021.8.12
 */
@Data
public class ReplaceCardParam extends UserCheckParam implements Serializable, Trimable {
    /**
     * 目标用户
     */
    String targetUser;

    /**
     * 旧卡名
     */
    String oldCardName;

    /**
     * 新卡名
     */
    String newCardName;

    /**
     * 替换数量
     */
    int count;
}
