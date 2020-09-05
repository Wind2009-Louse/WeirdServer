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
     * 根据用户名和密码在数据库查找
     *
     * @param name     用户名
     * @param password 密码
     * @return 结果
     */
    UserDataModel selectByNamePassword(
            @Param("name") String name,
            @Param("password") String password);

    /**
     * 根据用户名查找用户列表
     *
     * @param name 用户名
     * @return 用户列表
     */
    List<UserDataModel> selectByName(@Param("name") String name);

    /**
     * 根据用户名查找用户列表
     *
     * @param name 用户名
     * @return 用户
     */
    UserDataModel selectByNameDistinct(@Param("name") String name);
}