package com.weird.handler;

import com.weird.service.RecordService;
import com.weird.service.TaskService;
import com.weird.utils.CacheUtil;
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
import java.util.Calendar;
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

    @Autowired
    RecordService recordService;

    @Async
    @Scheduled(cron = "0 0 0 * * ?")
    public void clearDaily() throws Exception {
        recordService.setRecord("日常更新", "【日常刷新】开始");
        int updateCount = taskService.updateDaily();
        recordService.setRecord("日常更新", "【日常刷新】更新{}条数据", updateCount);
        CacheUtil.clearRollListWithDetailCache();
        CacheUtil.clearCardOwnListCache();
    }

    @Async
    @Scheduled(cron = "0 0 0 ? * 1")
    public void clearWeekly() throws Exception {
        recordService.setRecord("周常更新", "【周常刷新】开始");
        int updateCount = taskService.updateWeekly();
        recordService.setRecord("周常更新", "【周常刷新】更新{}条记录", updateCount);
    }

    @Async
    @Scheduled(cron = "30 0 0 * * ?")
    public void backupDataBase() throws Exception {
        recordService.setRecord("数据库备份", "【数据库备份】开始");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        String newDateString = formatter.format(currentDate);

        Calendar oldDateCalender = Calendar.getInstance();
        oldDateCalender.setTime(currentDate);
        oldDateCalender.add(Calendar.DATE, -14);
        String oldDateString = formatter.format(oldDateCalender.getTime());
        if (Files.deleteIfExists(Paths.get(String.format("backup/data-%s.db", oldDateString)))) {
            recordService.setRecord("数据库备份", "【数据库备份】清除{}时备份的数据库", oldDateString);
        }

        File dir = new File("backup");
        if (!dir.exists()) {
            dir.mkdir();
        }
        Path source = Paths.get("data.db");
        Files.copy(source, new FileOutputStream(String.format("backup/data-%s.db", newDateString)));
        recordService.setRecord("数据库备份", "【数据库备份】结束");
    }
}
