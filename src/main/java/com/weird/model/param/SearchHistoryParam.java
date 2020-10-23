package com.weird.model.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 修改历史查询参数
 *
 * @author Nidhogg
 * @date 2020.10.21
 */
@Data
public class SearchHistoryParam implements Serializable {
    /**
     * 卡包名
     */
    String packageName;

    /**
     * 卡片名
     */
    String cardName;

    /**
     * 当前页码
     */
    int page;

    /**
     * 页面大小
     */
    int pageSize;

    /**
     * 稀有度列表
     */
    List<String> rareList;
}
