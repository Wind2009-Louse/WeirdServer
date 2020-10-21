package com.weird.mapper.main;

import com.weird.model.UserCardListModel;
import com.weird.model.dto.CardListDTO;
import com.weird.model.dto.CardOwnListDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserCardListMapper {
    int insert(UserCardListModel record);

    int insertSelective(UserCardListModel record);

    /**
     * 根据用户和卡片搜索
     *
     * @param userId 用户ID
     * @param cardPk 卡片PK
     * @return 搜索结果
     */
    UserCardListModel selectByUserCard(@Param("userId") int userId,
                                       @Param("cardPk") int cardPk);

    /**
     * 根据用户和卡片更新信息
     *
     * @param model 模型
     * @return 更新数量
     */
    int update(UserCardListModel model);

    /**
     * 管理端查找卡片
     *
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param rareList    稀有度列表
     * @return 查询结果
     */
    List<CardListDTO> selectCardListAdmin(@Param("packageName") String packageName,
                                          @Param("cardName") String cardName,
                                          @Param("rareList") List<String> rareList);

    /**
     * 客户端查找卡片
     *
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param rareList    稀有度列表
     * @return 查询结果
     */
    List<CardListDTO> selectCardListUser(@Param("packageName") String packageName,
                                         @Param("cardName") String cardName,
                                         @Param("rareList") List<String> rareList,
                                         @Param("cardPk") int cardPk);

    /**
     * 查找卡片持有情况
     *
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param rare        稀有度
     * @param userName    用户名
     * @return 查询结果
     */
    List<CardOwnListDTO> selectCardOwnList(@Param("packageName") String packageName,
                                           @Param("cardName") String cardName,
                                           @Param("rare") String rare,
                                           @Param("userName") String userName);

    /**
     * 查找卡片的拥有数量
     *
     * @param cardPk 卡片Pk
     * @return 卡片拥有数量
     */
    int selectCardOwnCount(@Param("cardPk") int cardPk);

    /**
     * 批量插入记录
     *
     * @param list 记录
     * @return 插入数量
     */
    int insertBatch(@Param("list") List<UserCardListModel> list);
}