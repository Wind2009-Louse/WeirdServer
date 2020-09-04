package com.weird.service.impl;

import com.weird.mapper.UserDataMapper;
import com.weird.model.UserDataModel;
import com.weird.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    UserDataMapper userDataMapper;

    @Override
    public List<Integer> getNameById(String name) {
        return userDataMapper.selectByName(name);
    }

    @Override
    public String getNameById(int id) {
        UserDataModel model = userDataMapper.selectByPrimaryKey(id);
        if (model == null){
            return "";
        }
        return model.getUserName();
    }

    @Override
    public boolean checkLogin(String name, String encryptedPassword) {
        return (userDataMapper.selectByLogin(name, encryptedPassword) != null);
    }
}
