package com.weird.service.impl;

import com.weird.mapper.PackageCardMapper;
import com.weird.mapper.UserDataMapper;
import com.weird.model.PackageCardModel;
import com.weird.model.UserDataModel;
import com.weird.model.dto.UserDataDTO;
import com.weird.model.enums.LoginTypeEnum;
import com.weird.service.UserService;
import com.weird.utils.BeanConverter;
import com.weird.utils.OperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 用户Service实现
 *
 * @author Nidhogg
 * @date 2020.9.9
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    UserDataMapper userDataMapper;

    @Autowired
    PackageCardMapper packageCardMapper;

    final String DEFAULT_PASSWORD = "123456";
    final String DEFAULT_PASSWORD_MD5 = "E10ADC3949BA59ABBE56E057F20F883E";
    final List<String> NR_RARE = Arrays.asList("N", "R");

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
        if (modelList != null) {
            for (UserDataModel model : modelList) {
                if (model.getUserName().equals(name)) {
                    throw new OperationException("该用户：[%s]已存在！", name);
                }
            }
        }

        UserDataModel newModel = new UserDataModel();
        newModel.setUserName(name);
        newModel.setPassword(DEFAULT_PASSWORD_MD5);
        newModel.setIsAdmin((byte) 0);
        newModel.setDustCount(0);
        newModel.setDuelPoint(0);
        newModel.setNonawardCount(0);
        userDataMapper.insert(newModel);
        log.warn("添加新用户：[{}]", name);
        return newModel.getUserId() > 0;
    }

    /**
     * 修改用户密码
     *
     * @param name        用户名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否更改成功
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean updatePassword(String name, String oldPassword, String newPassword) throws Exception {
        UserDataModel model = userDataMapper.selectByNamePassword(name, oldPassword);
        if (model == null) {
            throw new OperationException("用户名或密码错误！");
        }
        model.setPassword(newPassword);
        log.warn("[{}]的密码发生修改", name);
        return userDataMapper.updateByPrimaryKey(model) > 0;
    }

    /**
     * 修改用户尘数
     *
     * @param name     用户名
     * @param newCount 新尘数
     * @return 是否更改成功
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean updateDust(String name, int newCount) throws Exception {
        UserDataModel model = userDataMapper.selectByNameDistinct(name);
        if (model == null) {
            throw new OperationException("找不到用户：[%s]！", name);
        }


        log.warn("[{}]的尘被修改：（{}->{}）", name, model.getDustCount(), newCount);
        model.setDustCount(newCount);
        userDataMapper.updateByPrimaryKey(model);
        return true;
    }

    /**
     * 暂时不做
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    @Deprecated
    public boolean dustToCard(String cardName, String userName, String password) throws Exception {
        // 玩家权限验证
        UserDataModel model = userDataMapper.selectByNamePassword(userName, password);
        if (model == null) {
            throw new OperationException("登录失败！");
        }

        PackageCardModel cardModel = packageCardMapper.selectByNameDistinct(cardName);
        if (cardModel == null) {
            throw new OperationException("找不到卡片：[%s]！", cardName);
        }
        int needDust = 0;
        if (NR_RARE.contains(cardModel.getRare())) {
            if (model.getWeeklyDustChangeN() >= 10){
                throw new OperationException("[%s]的每周更换次数已用完！", userName);
            }
            needDust = 15;
        } else {
            if (model.getWeeklyDustChangeAlter() > 0){
                throw new OperationException("[%s]的每周更换次数已用完！", userName);
            }
            needDust = 300;
        }
        if (model.getDustCount() < needDust){
            throw new OperationException("合成[%s]需要[%d]尘，当前[%s]拥有[%d]尘！", cardName, needDust, userName, model.getDustCount());
        }

        // TODO
        // 判断卡片是否已经拥有3张

        // TODO
        // 进行转换操作

        return false;
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
        if (model == null) {
            throw new OperationException("找不到用户：[%s]！", name);
        }

        log.warn("[{}]的月见黑被修改：（{}->{}）", name, model.getNonawardCount(), newCount);
        model.setNonawardCount(newCount);
        userDataMapper.updateByPrimaryKey(model);
        return true;
    }

    /**
     * 修改DP数量
     *
     * @param name     用户名
     * @param newCount 新结果
     * @return 是否更改成功
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean updateDuelPoint(String name, int newCount) throws Exception {
        UserDataModel model = userDataMapper.selectByNameDistinct(name);
        if (model == null) {
            throw new OperationException("找不到用户：[%s]！", name);
        }

        log.warn("[{}]的DP被修改：（{}->{}）", name, model.getDuelPoint(), newCount);
        model.setDuelPoint(newCount);
        userDataMapper.updateByPrimaryKey(model);
        return true;
    }
}
