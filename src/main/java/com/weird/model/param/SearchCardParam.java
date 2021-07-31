package com.weird.model.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.weird.interfaces.Fixable;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * 搜索卡片参数
 *
 * @author Nidhogg
 * @date 2020.10.21
 */
@Data
@ToString
public class SearchCardParam implements Serializable, Fixable {
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

    /**
     * 是否在收藏中搜索
     */
    boolean searchInCollection;

    @Override
    public void fix() {
        if (this.packageNameList == null) {
            this.packageNameList = new LinkedList<>();
        }
        if (this.cardName == null) {
            this.cardName = "";
        }
        if (this.targetUserList == null) {
            this.targetUserList = new LinkedList<>();
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
