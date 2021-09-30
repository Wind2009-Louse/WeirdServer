package com.weird.service.impl;

import com.weird.facade.BroadcastFacade;
import com.weird.mapper.main.UserDataMapper;
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

    @Autowired
    BroadcastFacade broadcastFacade;

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
        broadcastFacade.sendMsgAsync("【每周重置】新的一周了，合成次数和转盘次数已重置，快来试试运气吧！");
        return userDataMapper.updateWeekly();
    }
}
