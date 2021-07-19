package com.weird.handler;

import com.weird.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class RunnerHandler implements CommandLineRunner {
    @Autowired
    RecordService recordService;

    @Override
    public void run(String... args) throws Exception {
        recordService.setRecord("system", "后端进程启动");
    }
}
