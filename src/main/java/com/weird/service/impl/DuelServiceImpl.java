package com.weird.service.impl;

import com.weird.mapper.main.DuelHistoryMapper;
import com.weird.model.DuelHistoryModel;
import com.weird.model.param.DuelHistoryParam;
import com.weird.service.DuelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link DuelService}的实现
 *
 * @author Nidhogg
 * @date 2021.11.24
 */
@Service
@Slf4j
public class DuelServiceImpl implements DuelService {
    @Autowired
    DuelHistoryMapper duelHistoryMapper;

    @Override
    public long addDuelHistory(DuelHistoryModel duelHistoryModel) {
        duelHistoryMapper.insertSelective(duelHistoryModel);
        return duelHistoryModel.getDuelId();
    }

    @Override
    public List<DuelHistoryModel> searchByParam(DuelHistoryParam param) {
        return duelHistoryMapper.getByParam(param);
    }
}
