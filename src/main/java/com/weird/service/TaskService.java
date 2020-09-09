package com.weird.service;

/**
 * 任务Service
 *
 * @author Nidhogg
 * @date 2020.9.9
 */
public interface TaskService {
    /**
     * 更新日常统计
     *
     * @return 更新条目数量
     */
    int updateDaily() throws Exception;

    /**
     * 更新周常统计
     *
     * @return 更新条目数量
     */
    int updateWeekly() throws Exception;
}
