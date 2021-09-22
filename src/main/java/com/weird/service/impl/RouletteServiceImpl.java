package com.weird.service.impl;

import com.alibaba.fastjson.JSON;
import com.weird.mapper.main.RouletteMapper;
import com.weird.mapper.main.UserDataMapper;
import com.weird.model.RouletteConfigModel;
import com.weird.model.UserDataModel;
import com.weird.model.dto.RouletteConfigDTO;
import com.weird.model.dto.RouletteResultDTO;
import com.weird.service.RecordService;
import com.weird.service.RouletteService;
import com.weird.utils.BeanConverter;
import com.weird.utils.OperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Random;

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

    @Override
    public RouletteResultDTO roulette(String userName) throws OperationException {
        UserDataModel user = userDataMapper.selectByNameInAllDistinct(userName);
        if (user == null) {
            throw new OperationException("找不到用户：[%s]！", userName);
        }

        Byte isAdmin = user.getIsAdmin();
        int rouletteCount = (isAdmin > 0) ? 1 : user.getRoulette();
        if (rouletteCount <= 0) {
            throw new OperationException("你的转盘次数已用完！");
        }

        List<RouletteConfigDTO> configList = rouletteMapper.selectConfigList();
        if (CollectionUtils.isEmpty(configList)) {
            throw new OperationException("当前没有转盘配置，请联系管理员！");
        }
        final int rateSum = configList.stream().mapToInt(RouletteConfigDTO::getRate).sum();

        Random rd = new Random();
        int randRate = rd.nextInt(rateSum);
        int currentRate = randRate;

        // 根据随机值判断转盘结果
        for (int index = 0; index < configList.size(); ++index) {
            RouletteConfigDTO config = configList.get(index);
            currentRate -= config.getRate();
            if (currentRate >= 0) {
                continue;
            }

            RouletteResultDTO result = new RouletteResultDTO();
            result.setIndex(index);
            result.setResult(String.format("抽奖结果：%s", config.getDetail()));

            if (user.getIsAdmin() <= 0) {
                user.setRoulette(rouletteCount - 1);
                int updateCount = userDataMapper.updateByPrimaryKey(user);
                if (updateCount <= 0) {
                    throw new OperationException("用户数据更新失败！");
                }
            }

            recordService.setRecord(userName, "转盘抽奖结果:%s(%d),当前次数:%d",
                    config.getDetail(), randRate, rouletteCount);
            try {
                rouletteMapper.addHistory(userName, config.getDetail());
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            return result;
        }
        throw new OperationException("转盘失败，请重试！");
    }
}
