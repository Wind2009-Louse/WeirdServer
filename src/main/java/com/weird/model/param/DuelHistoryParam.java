package com.weird.model.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.weird.interfaces.Fixable;
import lombok.Data;

import java.io.Serializable;

/**
 * 查询决斗历史的参数
 *
 * @author Nidhogg
 * @date 2021.11.24
 */
@Data
public class DuelHistoryParam extends UserCheckParam implements Serializable, Fixable {
    /**
     * 玩家名
     */
    String player;

    /**
     * 开始时间
     */
    long startTime;

    /**
     * 结束时间
     */
    long endTime;

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

    @Override
    public void fix() {
        if (this.page == 0) {
            this.page = 1;
        }
        if (this.pageSize == 0) {
            this.pageSize = 20;
        }
    }
}
