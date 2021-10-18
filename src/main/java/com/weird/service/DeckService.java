package com.weird.service;

import com.weird.model.dto.DeckInfoDTO;
import com.weird.model.dto.DeckListDTO;
import com.weird.model.param.DeckInfoParam;
import com.weird.model.param.DeckListParam;
import com.weird.model.param.DeckShareParam;
import com.weird.model.param.DeckSubmitParam;

import java.util.List;

/**
 * 卡组Service
 *
 * @author Nidhogg
 * @date 2021.8.14
 */
public interface DeckService {
    /**
     * 搜索自己的卡组列表
     *
     * @param param 参数
     * @return
     * @throws Exception
     */
    List<DeckListDTO> searchPage(DeckListParam param) throws Exception;

    /**
     * 搜索所有卡组列表
     *
     * @param param 参数
     * @return
     * @throws Exception
     */
    List<DeckListDTO> searchPageAdmin(DeckListParam param) throws Exception;

    /**
     * 添加卡组
     *
     * @param param 参数
     * @return
     * @throws Exception
     */
    boolean addDeck(DeckSubmitParam param) throws Exception;

    /**
     * 修改卡组
     *
     * @param param 参数
     * @return
     * @throws Exception
     */
    boolean updateDeck(DeckSubmitParam param) throws Exception;

    /**
     * 获取卡组信息
     *
     * @param param
     * @return
     * @throws Exception
     */
    DeckInfoDTO getDeckInfo(DeckInfoParam param, boolean isAdmin) throws Exception;

    /**
     * 重命名卡组
     *
     * @param param
     * @return
     * @throws Exception
     */
    boolean renameDeck(DeckSubmitParam param) throws Exception;

    /**
     * 删除卡组
     *
     * @param param
     * @return
     * @throws Exception
     */
    boolean removeDeck(DeckSubmitParam param) throws Exception;

    /**
     * 分享卡组
     *
     * @param param
     * @return
     * @throws Exception
     */
    String shareDeck(DeckShareParam param, boolean isAdmin) throws Exception;

    /**
     * 管理员修改卡片名称时，更新用户的卡组
     *
     * @param oldCode
     * @param newCode
     * @param newType
     * @return
     */
    void updateDeckCardWhenRenamed(long oldCode, long newCode, int newType);

    /**
     * 用户卡片减少时，修改卡组中的卡片持有数量
     *
     * @param userId
     * @param cardCode
     * @param newCount
     */
    void updateDeckCardCountWhenUpdateCount(long userId, long cardCode, int newCount);
    void updateDeckCardCountWhenUpdateCount(String userName, long cardCode, int newCount);

    /**
     * 根据卡组ID获得卡组信息
     * @param deckId
     * @return
     */
    DeckInfoDTO getDeckById(int deckId);
}
