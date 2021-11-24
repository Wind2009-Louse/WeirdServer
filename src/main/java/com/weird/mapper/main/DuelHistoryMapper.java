package com.weird.mapper.main;

import com.weird.model.DuelHistoryModel;
import com.weird.model.param.DuelHistoryParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 决斗历史Mapper
 *
 * @author Nidhogg
 * @date 2021.11.24
 */
public interface DuelHistoryMapper {
    int insertSelective(DuelHistoryModel model);

    DuelHistoryModel getById(int id);

    List<DuelHistoryModel> getByParam(@Param("param") DuelHistoryParam param);
}
