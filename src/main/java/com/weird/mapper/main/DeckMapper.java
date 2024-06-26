package com.weird.mapper.main;

import com.weird.model.DeckDetailModel;
import com.weird.model.DeckListModel;
import com.weird.model.dto.DeckInfoDTO;
import com.weird.model.dto.DeckListDTO;
import com.weird.model.param.DeckListParam;
import com.weird.model.param.DeckSubmitParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 卡组Mapper
 *
 * @author Nidhogg
 * @date 2021.8.14
 */
public interface DeckMapper {
    /**
     * 搜索自己的卡组列表
     *
     * @param param 参数
     * @return
     * @throws Exception
     */
    List<DeckListDTO> selectDeckList(@Param("param") DeckListParam param);

    /**
     * 搜索所有卡组列表
     *
     * @param param 参数
     * @return
     * @throws Exception
     */
    List<DeckListDTO> selectDeckListAdmin(@Param("param") DeckListParam param);

    /**
     * 添加卡组
     *
     * @param param
     * @return
     */
    int addDeck(@Param("param") DeckInfoDTO param);

    /**
     * 修改卡组
     * @param param
     * @return
     */
    int updateDeck(@Param("param") DeckInfoDTO param);

    /**
     * 修改卡组分享状态
     *
     * @param deckId
     * @param share
     * @return
     */
    int updateDeckShare(@Param("deckId") long deckId, @Param("share") int share);

    /**
     * 添加卡组中的卡片
     * @param list
     * @return
     */
    int batchAddDeckCard(@Param("list") List<DeckDetailModel> list);

    /**
     * 检查ID对应的卡组是否存在
     *
     * @param deckId
     * @return
     */
    DeckListModel getDeckListInfoByDeckId(@Param("deckId") int deckId);

    /**
     * 删除卡组
     * @param deckId
     * @return
     */
    int deleteDeckByDeckId(@Param("deckId") int deckId);

    /**
     * 删除卡组卡片
     * @param deckId
     * @return
     */
    int deleteCardByDeckId(@Param("deckId") int deckId);

    /**
     * 获取卡组下的卡片信息
     *
     * @param deckId
     * @return
     */
    List<DeckDetailModel> getDeckDetailByDeckId(@Param("deckId") int deckId);

    /**
     * 更新卡组
     *
     * @param oldCode
     * @param newCode
     * @param newType
     * @return
     */
    int updateDeckCodeStepA(@Param("oldCode") long oldCode, @Param("newCode") long newCode, @Param("newType") int newType);
    int updateDeckCodeStepB(@Param("oldCode") long oldCode, @Param("newCode") long newCode);

    /**
     * 获取卡组下的卡片信息
     *
     * @return
     */
    List<DeckDetailModel> getDetailWhenChangeCount(@Param("userId") long userId, @Param("code") long code, @Param("count") int count);
    int updateDeckCardCount(@Param("count") int count, @Param("pkList") List<Long> pkList);
    int deleteDeckCardCount(@Param("pkList") List<Long> pkList);
}
