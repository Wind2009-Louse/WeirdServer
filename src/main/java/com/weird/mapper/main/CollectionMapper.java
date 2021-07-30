package com.weird.mapper.main;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CollectionMapper {
    List<Integer> getCollectionByUserId(@Param("userId") int userId);

    List<Integer> checkCollection(@Param("userId") int userId,
                                  @Param("cardPk") int cardPk);

    int addCollection(@Param("userId") int userId,
                      @Param("cardPk") int cardPk);

    int delCollection(@Param("userId") int userId,
                      @Param("cardPk") int cardPk);

    int cutOffCollection(@Param("userId") int userId,
                         @Param("cardPkList") List<Integer> cardPkList);
}
