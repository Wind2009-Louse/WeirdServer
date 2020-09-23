package com.weird.handler;

import com.weird.service.RollService;
import com.weird.service.TaskService;
import com.weird.service.impl.CardServiceImpl;
import com.weird.service.impl.RollServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        RollServiceImpl.clearRollListCache();
        CardServiceImpl.clearCardListCache();
    }

    @Async
    @Scheduled(cron = "0 0 0 ? * 1")
    public void clearWeekly() throws Exception {
        log.info("【周常刷新】开始");
        int updateCount = taskService.updateWeekly();
        log.info("【周常刷新】更新{}条记录", updateCount);
    }

    @Async
    @Scheduled(cron = "30 0 0 * * ?")
    public void backupDB() throws Exception {
        log.info("【数据库备份】开始");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(new Date());
        File dir = new File("backup");
        if (!dir.exists()) {
            dir.mkdir();
        }
        Path source = Paths.get("data.db");
        Files.copy(source, new FileOutputStream(String.format("backup/data.db-%s", dateString)));
        log.info("【数据库备份】结束");
    }
}
