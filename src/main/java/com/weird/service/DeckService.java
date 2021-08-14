package com.weird.service;

import com.weird.model.dto.DeckInfoDTO;
import com.weird.model.dto.DeckListDTO;
import com.weird.model.param.DeckInfoParam;
import com.weird.model.param.DeckListParam;
import com.weird.model.param.DeckSubmitParam;
import com.weird.utils.PageResult;

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
}
