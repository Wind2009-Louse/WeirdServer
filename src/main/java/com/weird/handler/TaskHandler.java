package com.weird.handler;

import com.weird.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

/**
 * 定时任务Handler
 *
 * @author Nidhogg
 * @date 2020.9.9
 */
@Configuration
@EnableAsync
@Slf4j
public class TaskHandler {
    @Autowired
    TaskService taskService;

    @Async
    @Scheduled(cron = "0 0 0 * * ?")
    public void clearDaily() throws Exception {
        log.info("【日常刷新】开始");
        int updateCount = taskService.updateDaily();
        log.info("【日常刷新】更新{}条数据", updateCount);
    }

    @Async
    @Scheduled(cron = "0 0 0 ? * 2")
    public void clearWeekly() throws Exception {
        log.info("【周常刷新】开始");
        int updateCount = taskService.updateWeekly();
        log.info("【周常刷新】更新{}条记录", updateCount);
    }
}
