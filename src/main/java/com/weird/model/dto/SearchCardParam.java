package com.weird.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 搜索卡片参数
 *
 * @author Nidhogg
 * @date 2020.10.21
 */
@Data
public class SearchCardParam implements Serializable {
    /**
     * 卡包名
     */
    @JsonProperty(defaultValue = "")
    String packageName;

    /**
     * 卡片名
     */
    @JsonProperty(defaultValue = "")
    String cardName;

    /**
     * 稀有度
     */
    List<String> rareList;

    /**
     * 当前页码
     */
    @JsonProperty(defaultValue = "1")
    int page;

    /**
     * 页面大小
     */
    @JsonProperty(defaultValue = "20")
    int pageSize;

    /**
     * 操作用户名称
     */
    String name;

    /**
     * 操作用户密码
     */
    String password;
}
