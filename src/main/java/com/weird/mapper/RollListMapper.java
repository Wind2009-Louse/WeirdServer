package com.weird.mapper;

import com.weird.model.RollListModel;
import com.weird.model.dto.RollListDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RollListMapper {
    int deleteByPrimaryKey(Integer rollId);

    int insert(RollListModel record);

    int insertSelective(RollListModel record);

    RollListModel selectByPrimaryKey(long rollId);

    int updateByPrimaryKeySelective(RollListModel record);

    int updateByPrimaryKey(RollListModel record);

    List<RollListDTO> selectByParam(@Param("packageName") String packageName,
                                    @Param("userName") String userName);
}