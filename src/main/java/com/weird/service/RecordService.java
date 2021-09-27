package com.weird.service;

import com.weird.model.RecordModel;
import com.weird.model.param.RecordParam;
import com.weird.utils.PageResult;

/**
 * 操作记录服务
 *
 * @author Nidhogg
 * @date 2021.7.17
 */
public interface RecordService {
    /**
     * 记录操作日志
     *
     * @param userName  操作人
     * @param operation 操作内容
     */
    void setRecord(String userName, String operation);

    /**
     * 记录操作日志
     *
     * @param userName 操作人
     * @param format   操作内容格式
     * @param args     格式化参数
     */
    void setRecord(String userName, String format, Object... args);

    /**
     * 搜索操作日志
     *
     * @param param
     * @return
     */
    PageResult<RecordModel> searchPage(RecordParam param) throws Exception;
}
