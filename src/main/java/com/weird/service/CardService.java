package com.weird.service;

import com.weird.model.dto.CardHistoryDTO;
import com.weird.model.dto.CardListDTO;
import com.weird.model.dto.CardOwnListDTO;

import java.util.List;

public interface CardService {
    /**
     * 修改用户持有的卡片数量
     *
     * @param userName    用户名
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param count       新的卡片数量
     * @return 是否修改成功
     */
    boolean updateCardCount(String userName, String packageName, String cardName, int count) throws Exception;

    /**
     * 管理端根据条件筛选所有卡片
     *
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param rare        稀有度
     * @return 查询结果
     */
    List<CardListDTO> selectListAdmin(String packageName, String cardName, String rare);

    /**
     * 根据条件筛选拥有的卡片
     *
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param rare        稀有度
     * @param userName    用户名
     * @return 查询结果
     */
    List<CardOwnListDTO> selectList(String packageName, String cardName, String rare, String userName);

    /**
     * 根据条件筛选卡片的历史记录
     *
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param rare        稀有度
     * @return 查询结果
     */
    List<CardHistoryDTO> selectHistory(String packageName, String cardName, String rare);
}
