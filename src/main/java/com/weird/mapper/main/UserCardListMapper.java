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
     * @param packageNameList 卡包名列表
     * @param cardNameList    卡片名列表
     * @param rareList        稀有度列表
     * @return 查询结果
     */
    List<CardListDTO> selectCardListAdmin(@Param("packageNameList") List<String> packageNameList,
                                          @Param("cardNameList") List<String> cardNameList,
                                          @Param("rareList") List<String> rareList);

    /**
     * 客户端查找卡片
     *
     * @param packageNameList 卡包名列表
     * @param cardNameList    卡片名列表
     * @param rareList        稀有度列表
     * @param cardPk          不包括在内的卡片ID
     * @return 查询结果
     */
    List<CardListDTO> selectCardListUser(@Param("packageNameList") List<String> packageNameList,
                                         @Param("cardNameList") List<String> cardNameList,
                                         @Param("rareList") List<String> rareList,
                                         @Param("name") String userName,
                                         @Param("cardPk") int cardPk);

    List<CardListDTO> selectCardListCollection(@Param("name") String userName,
                                               @Param("cardPkList") List<Integer> cardPkList);

    /**
     * 获取玩家端可以查阅的卡片主键列表
     *
     * @return
     */
    List<Integer> getVisibleCardPkList();

    /**
     * 查找卡片持有情况
     *
     * @param packageNameList 卡包名列表
     * @param cardNameList    卡片名列表
     * @param rareList        稀有度列表
     * @param userNameList    用户名列表
     * @return
     */
    List<CardOwnListDTO> selectCardOwnList(@Param("packageNameList") List<String> packageNameList,
                                           @Param("cardNameList") List<String> cardNameList,
                                           @Param("rareList") List<String> rareList,
                                           @Param("userNameList") List<String> userNameList);

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