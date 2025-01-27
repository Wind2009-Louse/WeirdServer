package com.weird.mapper.main;

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

    UserDataModel selectByNameInAllDistinct(@Param("name") String name);

    UserDataModel selectByQQInAllDistinct(@Param("qq") String qq);

    int clearQQ(@Param("qq") String qq);

    /**
     * 更新日常统计
     *
     * @return 更新条目数量
     */
    int updateDaily();

    /**
     * 更新周常统计
     *
     * @return 更新条目数量
     */
    int updateWeekly();

    /**
     * 设置用户禁用状态
     *
     * @param target   对象
     * @param disabled 是否禁用
     * @return
     */
    int updateDisabled(@Param("target") String target,
                       @Param("disabled") int disabled);

    /**
     * 显示禁用中的用户名列表
     * @return
     */
    List<String> showDisabled();

    /**
     * 查询用户剩余百八次数
     *
     * @param name
     * @return
     */
    int selectDoubleRareCount(@Param("name") String name);

    /**
     * 更新用户剩余百八次数
     *
     * @param name
     * @param count
     * @return
     */
    int updateDoubleRareCount(@Param("name") String name,
                              @Param("count") int count);

    /**
     * 每周重置百八次数
     * @return
     */
    int resetDoubleRareCount(@Param("count") int count);
}