package com.weird.service.impl;

import com.weird.mapper.UserDataMapper;
import com.weird.model.UserDataModel;
import com.weird.model.dto.UserDataDTO;
import com.weird.model.enums.LoginTypeEnum;
import com.weird.service.UserService;
import com.weird.utils.BeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDataMapper userDataMapper;

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
}
