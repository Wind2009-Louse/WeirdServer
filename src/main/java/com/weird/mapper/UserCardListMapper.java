package com.weird.mapper;

import com.weird.model.UserCardListModel;
import org.apache.ibatis.annotations.Param;

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
}