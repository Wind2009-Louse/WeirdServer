package com.weird.service;

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
}
