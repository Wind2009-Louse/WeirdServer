package com.weird.utils;

import lombok.Data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * 分页查询结果
 * 使用泛型声明类，便于编码的时候可以在编译阶段发现类型不匹配的问题。
 *
 * @author Sue Sobim
 */
@Data
public class PageResult<T> implements Serializable {
    /**
     * 默认配置的页面大小
     */
    static final int PAGE_SIZE = 20;

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

    /**
     * 设置页面信息，根据页码进行分页等操作
     *
     * @param list      要加入的页面内容
     * @param pageIndex 当前页码
     */
    public void addPageInfo(List<T> list, int pageIndex) throws Exception {
        addPageInfo(list, pageIndex, PAGE_SIZE);
    }

    /**
     * 设置页面信息，根据页码进行分页等操作
     *
     * @param list      要加入的页面内容
     * @param pageIndex 当前页码
     * @param pageSize 页面大小
     */
    public void addPageInfo(List<T> list, int pageIndex, int pageSize) throws Exception {
        if (pageIndex <= 0) {
            throw new OperationException("页码错误！");
        }
        totalCount = list.size();
        currPage = pageIndex;
        dataList = new LinkedList<>();
        this.pageSize = pageSize;
        // 复制
        for (int index = pageSize * (currPage - 1); index < totalCount && index < pageSize * currPage; ++index) {
            T item = list.get(index);
            if (item != null) {
                dataList.add(item);
            }
        }

        // 设置总页数
        if (totalCount % pageSize == 0) {
            totalPage = totalCount / pageSize;
        } else {
            totalPage = totalCount / pageSize + 1;
        }
    }
}
