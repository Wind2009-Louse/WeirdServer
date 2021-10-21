package com.weird.service.impl;

import com.weird.facade.RecordFacade;
import com.weird.mapper.main.ForbiddenMapper;
import com.weird.model.ForbiddenModel;
import com.weird.model.dto.ForbiddenDTO;
import com.weird.model.dto.ForbiddenInfoDTO;
import com.weird.service.ForbiddenService;
import com.weird.utils.BeanConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

/**
 * 禁限Service实现
 *
 * @author Nidhogg
 * @date 2021.10.21
 */
@Service
public class ForbiddenServiceImpl implements ForbiddenService {
    @Autowired
    ForbiddenMapper forbiddenMapper;

    @Autowired
    RecordFacade recordFacade;

    @Override
    public List<ForbiddenModel> selectAll() {
        return forbiddenMapper.selectAll();
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean update(ForbiddenDTO dto, String operator) {
        List<ForbiddenModel> insertList = new LinkedList<>();
        List<ForbiddenModel> limitList = BeanConverter.convertList(dto.getLimit().getInfoList(), ForbiddenModel.class);
        List<ForbiddenModel> semiLimitList = BeanConverter.convertList(dto.getSemiLimit().getInfoList(), ForbiddenModel.class);
        limitList.forEach(o -> o.setCount(1));
        semiLimitList.forEach(o -> o.setCount(2));
        insertList.addAll(limitList);
        insertList.addAll(semiLimitList);
        recordFacade.setRecord(operator, "限制卡列表更新为：[%s]", String.join(",", dto.getLimit().getNameList()));
        recordFacade.setRecord(operator, "准限制卡列表更新为：[%s]", String.join(",", dto.getSemiLimit().getNameList()));

        return forbiddenMapper.clearAll() >= 0 && forbiddenMapper.batchInsert(insertList) > 0;
    }
}
