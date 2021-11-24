package com.weird.service;

import com.weird.model.DuelHistoryModel;
import com.weird.model.param.DuelHistoryParam;

import java.util.List;

/**
 * 决斗报文Service
 *
 * @author Nidhogg
 * @date 2021.11.24
 */
public interface DuelService {
    /**
     * 添加决斗历史记录
     *
     * @param duelHistoryModel
     * @return
     */
    long addDuelHistory(DuelHistoryModel duelHistoryModel);

    /**
     * 根据参数查找决斗历史记录
     *
     * @param param
     * @return
     */
    List<DuelHistoryModel> searchByParam(DuelHistoryParam param);
}
