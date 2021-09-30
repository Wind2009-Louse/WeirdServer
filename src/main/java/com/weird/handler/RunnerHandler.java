package com.weird.handler;

import com.weird.facade.RecordFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 启动Handler，用于发送记录
 *
 * @author Nidhogg
 * @date 2021.7.19
 */
@Component
@Order(1)
public class RunnerHandler implements CommandLineRunner {
    @Autowired
    RecordFacade recordFacade;

    @Override
    public void run(String... args) throws Exception {
        recordFacade.setRecord("system", "后端进程启动");
    }
}
