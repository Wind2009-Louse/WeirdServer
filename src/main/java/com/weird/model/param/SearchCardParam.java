package com.weird.model.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 搜索卡片参数
 *
 * @author Nidhogg
 * @date 2020.10.21
 */
@Data
@ToString
public class SearchCardParam implements Serializable {
    /**
     * 卡包名
     */
    List<String> packageNameList;

    /**
     * 卡片名
     */
    @JsonProperty(defaultValue = "")
    String cardName;

    /**
     * 目标用户
     */
    List<String> targetUserList;

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
