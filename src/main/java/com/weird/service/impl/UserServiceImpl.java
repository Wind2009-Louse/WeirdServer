package com.weird.service.impl;

import com.weird.mapper.UserDataMapper;
import com.weird.model.UserDataModel;
import com.weird.model.dto.UserDataDTO;
import com.weird.model.enums.LoginTypeEnum;
import com.weird.service.UserService;
import com.weird.utils.BeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDataMapper userDataMapper;

    final String DEFAULT_PASSWORD = "123456";
    final String DEFAULT_PASSWORD_MD5 = "E10ADC3949BA59ABBE56E057F20F883E";

    /**
     * 根据用户名查找用户列表
     *
     * @param name 用户名
     * @return 用户列表
     */
    @Override
    public List<UserDataDTO> getListByName(String name) {
        List<UserDataModel> modelList = userDataMapper.selectByName(name);
        if (modelList == null) {
            return Collections.emptyList();
        }
        return BeanConverter.convertList(modelList, UserDataDTO.class);
    }

    /**
     * 根据用户ID查找用户
     *
     * @param id 用户ID
     * @return 用户
     */
    @Override
    public UserDataDTO getNameById(int id) {
        UserDataModel model = userDataMapper.selectByPrimaryKey(id);
        if (model == null) {
            return null;
        }
        return BeanConverter.convert(model, UserDataDTO.class);
    }

    /**
     * 根据用户名和密码检查登录类型
     *
     * @param name              用户名
     * @param encryptedPassword 密码
     * @return 登录类型
     */
    @Override
    public LoginTypeEnum checkLogin(String name, String encryptedPassword) {
        UserDataModel model = userDataMapper.selectByNamePassword(name, encryptedPassword);
        if (model == null) {
            return LoginTypeEnum.UNLOGIN;
        }
        if (model.getIsAdmin() == 1) {
            return LoginTypeEnum.ADMIN;
        } else {
            return LoginTypeEnum.NORMAL;
        }
    }

    /**
     * 添加新用户（默认密码为123456）
     *
     * @param name 用户名
     * @return 是否添加成功
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean addUser(String name) throws Exception {
        List<UserDataModel> modelList = userDataMapper.selectByName(name);
        if (modelList != null){
            for (UserDataModel model : modelList){
                if (model.getUserName().equals(name)){
                    return false;
                }
            }
        }

        UserDataModel newModel = new UserDataModel();
        newModel.setUserName(name);
        newModel.setPassword(DEFAULT_PASSWORD);
        newModel.setIsAdmin((byte) 0);
        newModel.setDustCount(0);
        newModel.setNonawardCount(0);
        userDataMapper.insert(newModel);
        return newModel.getUserId() > 0;
    }

    /**
     * 修改用户密码
     *
     * @param name 用户名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否更改成功
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean updatePassword(String name, String oldPassword, String newPassword) throws Exception {
        UserDataModel model = userDataMapper.selectByNamePassword(name, oldPassword);
        if (model == null){
            throw new Exception("用户名或密码错误！");
        }
        model.setPassword(newPassword);
        return userDataMapper.updateByPrimaryKey(model) > 0;
    }

    /**
     * 修改用户尘数
     *
     * @param name 用户名
     * @param newCount 新尘数
     * @return 是否更改成功
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean updateDust(String name, int newCount) throws Exception {
        UserDataModel model = userDataMapper.selectByNameDistinct(name);
        if (model == null){
            throw new Exception("找不到用户！");
        }

        model.setDustCount(newCount);
        userDataMapper.updateByPrimaryKey(model);
        return true;
    }

    /**
     * 修改不出货数量
     *
     * @param name     用户名
     * @param newCount 新结果
     * @return 是否更改成功
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean updateAward(String name, int newCount) throws Exception {
        UserDataModel model = userDataMapper.selectByNameDistinct(name);
        if (model == null){
            throw new Exception("找不到用户！");
        }

        model.setNonawardCount(newCount);
        userDataMapper.updateByPrimaryKey(model);
        return true;
    }
}
