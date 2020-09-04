package com.weird.model;

import lombok.Data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * 分页查询结果
 * 使用泛型声明类，便于编码的时候可以在编译阶段发现类型不匹配的问题。
 */
@Data
public class PageResult<T> implements Serializable {
    /**
     * 总条数
     */
    private int totalCount;
    /**
     * 总页数
     */
    private int totalPage;
    /**
     * 每页条数
     */
    private int pageSize;
    /**
     * 当前页
     */
    private int currPage;
    /**
     * 查询结果
     */
    private List<T> dataList;


    public void build() {
        if (totalCount % pageSize == 0) {
            totalPage = totalCount / pageSize;
        } else {
            totalPage = totalCount / pageSize + 1;
        }
    }
}
