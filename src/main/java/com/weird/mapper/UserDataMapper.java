package com.weird.mapper;

import com.weird.model.UserDataModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserDataMapper {
    int deleteByPrimaryKey(Integer userId);

    int insert(UserDataModel record);

    int insertSelective(UserDataModel record);

    UserDataModel selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(UserDataModel record);

    int updateByPrimaryKey(UserDataModel record);


    /**
     * 根据用户名和密码检查是否存在该用户
     * @param name 用户名
     * @param password 密码
     * @return 用户信息
     */
    UserDataMapper selectByLogin(@Param("name") String name,
                                 @Param("password") String password);

    /**
     * 根据用户名返回用户ID
     * @param name 用户名
     * @return 用户ID
     */
    List<Integer> selectByName(@Param("name") String name);
}