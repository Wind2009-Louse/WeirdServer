package com.weird.model.param;

import com.weird.interfaces.Fixable;
import lombok.Data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 搜索记录参数
 *
 * @author Nidhogg
 * @date 2021.7.17
 */
@Data
public class RecordParam extends UserCheckParam implements Serializable, Fixable {
    private static SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 操作人
     */
    String operator;

    /**
     * 操作内容
     */
    String detail;
    List<String> detailList;

    /**
     * 开始时间
     */
    long startTime;
    String startTimeStr;

    /**
     * 结束时间
     */
    long endTime;
    String endTimeStr;

    /**
     * 当前页码
     */
    int page;

    /**
     * 页面大小
     */
    int pageSize;

    @Override
    public void fix() {
        startTimeStr = null;
        endTimeStr = null;
        if (startTime > 0) {
            startTimeStr = fm.format(new Date(startTime * 1000));
        }
        if (endTime > 0) {
            endTimeStr = fm.format(new Date(endTime * 1000));
        }
        if (this.page <= 0) {
            this.page = 1;
        }
        if (this.pageSize <= 0) {
            this.pageSize = 20;
        }
    }
}
