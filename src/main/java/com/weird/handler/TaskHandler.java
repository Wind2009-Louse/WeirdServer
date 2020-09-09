package com.weird.handler;

import lombok.extern.slf4j.Slf4j;
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
    @Async
    @Scheduled(cron = "0 0 0 * * ?")
    public void clearDaily(){
        // TODO
        log.info("日常刷新");
    }

    @Async
    @Scheduled(cron = "0 0 0 ? * 2")
    public void clearWeekly(){
        // TODO
        log.info("周常刷新");
    }
}
