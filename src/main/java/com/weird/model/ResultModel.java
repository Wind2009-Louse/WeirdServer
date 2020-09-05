package com.weird.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 返回数据模型
 *
 * @author Nidhogg
 */
@Data
public class ResultModel<T> implements Serializable {
    int code;
    T data;

    /**
     * 初始化
     *
     * @param code 返回代码
     * @param data 数据
     */
    public ResultModel(int code, T data){
        this.code = code;
        this.data = data;
    }
}
