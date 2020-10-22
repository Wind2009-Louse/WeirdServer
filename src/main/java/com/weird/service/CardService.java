package com.weird.service;

import com.weird.model.dto.*;

import java.util.List;

public interface CardService {
    /**
     * 修改用户持有的卡片数量
     *
     * @param userName 用户名
     * @param cardName 卡片名
     * @param count    新的卡片数量
     * @return 是否修改成功
     */
    boolean updateCardCount(String userName, String cardName, int count) throws Exception;

    /**
     * 批量修改用户持有的卡片数量
     *
     * @param param 参数
     * @return 修改结果
     */
    String updateCardCountBatch(BatchUpdateUserCardParam param) throws Exception;

    /**
     * 管理端根据条件筛选所有卡片
     *
     * @param param 参数
     * @return 查询结果
     */
    List<CardListDTO> selectListAdmin(SearchCardParam param);

    /**
     * 玩家端根据条件筛选所有卡片
     *
     * @param param 参数
     * @return 查询结果
     */
    List<CardListDTO> selectListUser(SearchCardParam param);

    /**
     * 根据条件筛选拥有的卡片
     *
     * @param param 参数
     * @return 查询结果
     */
    List<CardOwnListDTO> selectList(SearchCardParam param);

    /**
     * 根据条件筛选卡片的历史记录
     *
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param rareList    稀有度列表
     * @return 查询结果
     */
    List<CardHistoryDTO> selectHistory(String packageName, String cardName, List<String> rareList);
}
