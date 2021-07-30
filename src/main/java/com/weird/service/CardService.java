package com.weird.service;

import com.weird.model.dto.*;
import com.weird.model.enums.LoginTypeEnum;
import com.weird.model.param.BatchUpdateUserCardParam;
import com.weird.model.param.CollectionParam;
import com.weird.model.param.SearchCardParam;
import com.weird.model.param.SearchHistoryParam;
import com.weird.utils.OperationException;

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
    String updateCardCount(String userName, String cardName, int count, String operator) throws Exception;

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
     * @param param    参数
     * @param cardList 卡名列表
     * @return 查询结果
     */
    List<CardListDTO> selectListAdmin(SearchCardParam param, List<String> cardList);

    /**
     * 玩家端根据条件筛选所有卡片
     *
     * @param param    参数
     * @param cardList 卡名列表
     * @return 查询结果
     */
    List<CardListDTO> selectListUser(SearchCardParam param, List<String> cardList);

    /**
     * 查看自己收藏的卡片信息
     *
     * @param param         搜索参数
     * @param loginTypeEnum 登录信息
     * @return 查询结果
     */
    List<CardListDTO> selectListCollection(CollectionParam param, LoginTypeEnum loginTypeEnum) throws OperationException;

    /**
     * 根据条件筛选拥有的卡片
     *
     * @param param    参数
     * @param cardList 卡名列表
     * @return 查询结果
     */
    List<CardOwnListDTO> selectList(SearchCardParam param, List<String> cardList);

    /**
     * 根据条件筛选卡片的历史记录
     *
     * @param param    参数
     * @param cardList 卡名列表
     * @return 查询结果
     */
    List<CardHistoryDTO> selectHistory(SearchHistoryParam param, List<String> cardList);
}
