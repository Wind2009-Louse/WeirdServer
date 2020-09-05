package com.weird.service;

import com.weird.model.dto.RollListDTO;

import java.util.List;

public interface RollService {
    /**
     * 根据用户名查找抽卡结果（只包含记录，结果未返回）
     *
     * @param packageName 卡包名
     * @param userName 用户名
     * @return 结果列表（内容需要查询）
     */
    List<RollListDTO> selectRollList(String packageName, String userName);

    /**
     * 根据抽卡结果返回详细内容
     *
     * @param list 抽卡结果
     * @return 带详细内容的抽卡结果
     */
    List<RollListDTO> selectRollDetail(List<RollListDTO> list) throws Exception;

    /**
     * 抽卡处理
     *
     * @param packageName 卡包名
     * @param cardNames 卡片名
     * @param userName 用户名
     * @return 是否记录成功
     */
    boolean roll(String packageName, List<String> cardNames, String userName) throws Exception;

    /**
     * 设置抽卡记录状态
     *
     * @param rollId 抽卡记录ID
     * @param newStatus 新的状态
     * @return 是否设置成功
     */
    boolean setStatus(long rollId, int newStatus) throws Exception;
}
