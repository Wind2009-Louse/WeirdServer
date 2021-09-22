package com.weird.model.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.weird.interfaces.Fixable;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页参数
 *
 * @author Nidhogg
 * @date 2021.9.22
 */
@Data
public class PageParam implements Serializable, Fixable {
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
