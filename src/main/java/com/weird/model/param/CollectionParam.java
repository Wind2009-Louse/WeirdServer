package com.weird.model.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.weird.interfaces.Fixable;
import com.weird.interfaces.Trimable;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * 收藏编辑参数
 *
 * @author Nidhogg
 * @date 2020.10.5
 */
@Data
public class CollectionParam extends UserCheckParam implements Serializable, Trimable, Fixable {
    /**
     * 卡片ID
     */
    String cardName;

    /**
     * 操作（1=添加，2=移除）
     */
    int op;

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
