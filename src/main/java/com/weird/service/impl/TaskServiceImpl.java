package com.weird.service.impl;

import com.weird.mapper.UserDataMapper;
import com.weird.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 任务Service实现
 *
 * @author Nidhogg
 * @date 2020.9.9
 */
@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    UserDataMapper userDataMapper;

    /**
     * 更新日常统计
     *
     * @return 更新条目数量
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public int updateDaily() throws Exception {
        return userDataMapper.updateDaily();
    }

    /**
     * 更新周常统计
     *
     * @return 更新条目数量
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public int updateWeekly() throws Exception {
        return userDataMapper.updateWeekly();
    }
}
