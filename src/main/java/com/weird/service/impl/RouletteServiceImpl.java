package com.weird.service.impl;

import com.alibaba.fastjson.JSON;
import com.weird.mapper.main.RouletteMapper;
import com.weird.mapper.main.UserDataMapper;
import com.weird.model.RouletteConfigModel;
import com.weird.model.dto.RouletteConfigDTO;
import com.weird.service.RecordService;
import com.weird.service.RouletteService;
import com.weird.utils.BeanConverter;
import com.weird.utils.OperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 转盘服务实现
 *
 * @author Nidhogg
 * @date 2021.9.22
 */
@Service
@Slf4j
public class RouletteServiceImpl implements RouletteService {
    @Autowired
    RouletteMapper rouletteMapper;

    @Autowired
    UserDataMapper userDataMapper;

    @Autowired
    RecordService recordService;

    @Override
    public List<RouletteConfigDTO> listConfig() {
        return rouletteMapper.selectConfigList();
    }

    @Override
    public String updateConfig(List<RouletteConfigDTO> list, String operator) throws OperationException {
        if (CollectionUtils.isEmpty(list)) {
            throw new OperationException("配置项不能为空！");
        }
        recordService.setRecord(operator,
                "转盘配置修改为%s",
                JSON.toJSONString(list));
        List<RouletteConfigModel> modelList = BeanConverter.convertList(list, RouletteConfigModel.class);
        rouletteMapper.clearConfig();
        int count = rouletteMapper.batchInsertConfig(modelList);
        if (count > 0) {
            return "修改成功！";
        } else {
            return "修改失败！";
        }
    }
}
