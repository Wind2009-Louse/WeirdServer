package com.weird.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.weird.mapper.main.RecordMapper;
import com.weird.model.RecordModel;
import com.weird.model.param.RecordParam;
import com.weird.service.RecordService;
import com.weird.utils.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

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
            if (StringUtils.isEmpty(userName)) {
                userName = "system";
            }

            RecordModel record = new RecordModel();
            record.setOperator(userName);
            record.setDetail(operation);

            recordMapper.insert(record);
        } catch (Exception e) {
            log.error("插入记录失败：",e);
        }
    }

    @Override
    public void setRecord(String userName, String format, Object... args) {
        try {
            String detail = String.format(format, args);
            setRecord(userName, detail);
        } catch (Exception e) {
            log.error("转换格式出错：", e);
        }
    }

    @Override
    public PageResult<RecordModel> searchPage(RecordParam param) throws Exception {
        PageResult<RecordModel> result = new PageResult<>();
        result.setCurrPage(param.getPage());
        result.setPageSize(param.getPageSize());

        int count = recordMapper.count(param);
        List<RecordModel> resultList;
        result.setTotalCount(count);
        if (count <= 0) {
            resultList = Collections.emptyList();
        } else {
            resultList = recordMapper.searchList(param);
        }

        result.addPageInfo(resultList, param.getPage(), param.getPageSize());
        return result;
    }
}
