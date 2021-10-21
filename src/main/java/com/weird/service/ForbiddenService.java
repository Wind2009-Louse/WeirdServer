package com.weird.service;

import com.weird.model.ForbiddenModel;
import com.weird.model.dto.ForbiddenDTO;

import java.util.List;

/**
 * 禁限Service
 *
 * @author Nidhogg
 * @date 2021.10.15
 */
public interface ForbiddenService {
    /**
     * 查询所有禁限信息
     *
     * @return
     */
    List<ForbiddenModel> selectAll();

    /**
     * 更新禁限信息
     *
     * @param list
     * @return
     */
    boolean update(ForbiddenDTO dto, String operator);
}
