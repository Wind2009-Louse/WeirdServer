package com.weird.mapper.main;

import com.weird.model.CardHistoryModel;
import com.weird.model.ForbiddenModel;
import com.weird.model.dto.CardHistoryDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 禁限Mapper
 *
 * @author Nidhogg
 * @date 2021.10.21
 */
public interface ForbiddenMapper {
    /**
     * 查询所有禁限信息
     * @return
     */
    List<ForbiddenModel> selectAll();

    /**
     * 清除所有禁限信息
     * @return
     */
    int clearAll();

    /**
     * 批量插入禁限信息
     * @param list
     * @return
     */
    int batchInsert(@Param("list") List<ForbiddenModel> list);
}
