package com.weird.model.param;

import com.weird.interfaces.Fixable;
import lombok.Data;

import java.io.Serializable;

/**
 * 搜索记录参数
 *
 * @author Nidhogg
 * @date 2021.7.17
 */
@Data
public class RecordParam extends UserCheckParam implements Serializable, Fixable {
    /**
     * 操作人
     */
    String operator;

    /**
     * 操作内容
     */
    String detail;

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
    int page;

    /**
     * 页面大小
     */
    int pageSize;

    int beginFrom;

    @Override
    public void fix() {
        if (this.page <= 0) {
            this.page = 1;
        }
        if (this.pageSize <= 0) {
            this.pageSize = 20;
        }
        this.beginFrom = (this.page - 1) * this.pageSize;
    }
}
