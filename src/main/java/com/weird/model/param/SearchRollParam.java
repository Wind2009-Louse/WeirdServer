package com.weird.model.param;

import com.weird.interfaces.Fixable;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * 搜索抽卡结果参数
 *
 * @author Nidhogg
 * @date 2020.10.23
 */
@Data
public class SearchRollParam implements Serializable, Fixable {
    /**
     * 卡包名列表
     */
    List<String> packageNameList;

    /**
     * 用户名列表
     */
    List<String> userNameList;

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

    @Override
    public void fix() {
        if (this.packageNameList == null) {
            this.packageNameList = new LinkedList<>();
        }
        if (this.userNameList == null) {
            this.userNameList = new LinkedList<>();
        }
        if (this.page == 0) {
            this.page = 1;
        }
        if (this.pageSize == 0) {
            this.pageSize = 20;
        }
    }
}
