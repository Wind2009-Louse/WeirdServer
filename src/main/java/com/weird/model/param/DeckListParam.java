package com.weird.model.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 查询卡组列表的参数
 *
 * @author Nidhogg
 * @date 2021.8.14
 */
@Data
public class DeckListParam extends UserCheckParam implements Serializable {
    /**
     * 卡组名
     */
    String deckName;

    /**
     * 用户名
     */
    String targetUser;

    /**
     * 排序方式（1-名称，2-最后修改时间）
     */
    int sortType;

    /**
     * 排序顺序（1-倒序）
     */
    int sortWay;

    /**
     * 搜索分享卡组
     */
    int share;

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
}
