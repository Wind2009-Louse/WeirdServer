package com.weird.model.param;

import com.weird.interfaces.Fixable;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * 修改历史查询参数
 *
 * @author Nidhogg
 * @date 2020.10.21
 */
@Data
public class SearchHistoryParam implements Serializable, Fixable {
    /**
     * 卡包名
     */
    List<String> packageNameList;

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

    @Override
    public void fix() {
        if (this.packageNameList == null) {
            this.packageNameList = new LinkedList<>();
        }
        if (this.cardName == null) {
            this.cardName = "";
        }
        if (this.rareList == null) {
            this.rareList = new LinkedList<>();
        }
        if (this.page == 0) {
            this.page = 1;
        }
        if (this.pageSize == 0) {
            this.pageSize = 20;
        }
    }
}
