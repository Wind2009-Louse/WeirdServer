package com.weird.service;

import com.weird.model.UserDataModel;
import com.weird.model.dto.UserDataDTO;
import com.weird.model.enums.LoginTypeEnum;

import java.util.List;

public interface UserService {
    /**
     * 根据用户名查找用户列表
     *
     * @param name 用户名
     * @return 用户列表
     */
    List<UserDataDTO> getListByName(String name);

    /**
     * 根据用户ID查找用户
     *
     * @param id 用户ID
     * @return 用户
     */
    UserDataDTO getNameById(int id);

    /**
     * 根据用户名和密码检查登录类型
     *
     * @param name              用户名
     * @param encryptedPassword 密码
     * @return 登录类型
     */
    LoginTypeEnum checkLogin(String name, String encryptedPassword);

    /**
     * 添加新用户（默认密码为123456）
     *
     * @param name 用户名
     * @return 是否添加成功
     */
    boolean addUser(String name);

    /**
     * 修改用户密码
     *
     * @param name 用户名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否更改成功
     */
    boolean updatePassword(String name, String oldPassword, String newPassword);
}
