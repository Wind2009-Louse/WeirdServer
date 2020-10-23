package com.weird.mapper.main;

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

    List<RollListDTO> selectByParam(@Param("packageNameList") List<String> packageNameList,
                                    @Param("userNameList") List<String> userNameList,
                                    @Param("startTime") String startTime,
                                    @Param("endTime") String endTime);
}