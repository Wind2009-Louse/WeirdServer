package com.weird.mapper.main;

import com.weird.model.RecordModel;
import com.weird.model.param.RecordParam;

import java.util.List;

/**
 * 记录Mapper
 *
 * @author Nidhogg
 * @date 2021.7.17
 */
public interface RecordMapper {
    int insert(RecordModel model);

    int count(RecordParam param);

    List<RecordModel> searchList(RecordParam param);
}
