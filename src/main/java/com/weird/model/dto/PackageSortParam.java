package com.weird.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 卡包排序参数
 *
 * @author Nidhogg
 * @date 2020.10.5
 */
@Data
public class PackageSortParam implements Serializable {
    /**
     * 用户名
     */
    String name;

    /**
     * 密码
     */
    String password;

    /**
     * 新的卡包顺序
     */
    List<Integer> packageIndexList;
}
