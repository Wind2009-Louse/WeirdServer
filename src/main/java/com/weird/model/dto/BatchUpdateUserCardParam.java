package com.weird.model.dto;

import com.weird.model.Trimable;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 批量更改用户卡片持有数量
 *
 * @author Nidhogg
 * @date 2020.9.23
 */
@Data
public class BatchUpdateUserCardParam implements Serializable, Trimable {
    /**
     * 用户名
     */
    String name;

    /**
     * 密码
     */
    String password;

    /**
     * 操作目标
     */
    String target;

    /**
     * 卡片持有数量
     */
    Map<String, Integer> counts;
}
