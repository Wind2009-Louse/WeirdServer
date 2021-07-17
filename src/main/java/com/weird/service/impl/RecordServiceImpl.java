package com.weird.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.weird.mapper.main.RecordMapper;
import com.weird.model.RecordModel;
import com.weird.service.RecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 操作记录服务实现
 *
 * @author Nidhogg
 * @date 2021.7.17
 */
@Service
@Slf4j
public class RecordServiceImpl implements RecordService {
    @Autowired
    RecordMapper recordMapper;

    @Override
    public void setRecord(String userName, String operation) {
        log.warn(operation);
        try {
            long currentTime = System.currentTimeMillis();
            if (StringUtils.isEmpty(userName)) {
                userName = "empty";
            }

            RecordModel record = new RecordModel();
            record.setOperator(userName);
            record.setText(operation);

            recordMapper.insert(record);
        } catch (Exception e) {
            log.error("插入记录失败：",e);
        }
    }
}
