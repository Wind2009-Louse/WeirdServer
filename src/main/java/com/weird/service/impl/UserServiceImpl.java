package com.weird.service.impl;

import com.weird.mapper.*;
import com.weird.model.*;
import com.weird.model.dto.CardListDTO;
import com.weird.model.dto.UserDataDTO;
import com.weird.model.enums.DustEnum;
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
import java.util.Random;

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
    UserCardListMapper userCardListMapper;

    @Autowired
    PackageCardMapper packageCardMapper;

    @Autowired
    PackageInfoMapper packageInfoMapper;

    @Autowired
    RollListMapper rollListMapper;

    @Autowired
    RollDetailMapper rollDetailMapper;

    final String DEFAULT_PASSWORD = "123456";
    final String DEFAULT_PASSWORD_MD5 = "e10adc3949ba59abbe56e057f20f883e";
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
        UserDataModel modelList = userDataMapper.selectByNameDistinct(name);
        if (modelList != null) {
            throw new OperationException("该用户：[%s]已存在！", name);
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
     * 将尘转换成卡片
     *
     * @param cardName 卡片名称
     * @param userName 用户名
     * @param password 密码
     * @return 是否转换成功
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean dustToCard(String cardName, String userName, String password) throws Exception {
        // 玩家权限验证
        UserDataModel userModel = userDataMapper.selectByNamePassword(userName, password);
        if (userModel == null) {
            throw new OperationException("登录失败！");
        }

        // 判断卡片是否可合成
        PackageCardModel cardModel = packageCardMapper.selectByNameDistinct(cardName);
        if (cardModel == null) {
            throw new OperationException("找不到卡片：[%s]！", cardName);
        }
        PackageInfoModel packageModel = packageInfoMapper.selectByPrimaryKey(cardModel.getPackageId());
        if (packageModel == null || "LEGEND".equals(packageModel.getPackageName())) {
            throw new OperationException("无法合成[%s]！");
        }

        List<CardListDTO> records = userCardListMapper.selectCardListUser(null, null, null, cardModel.getCardPk());
        if (records.size() <= 0) {
            throw new OperationException("找不到卡片：[%s]！", cardName);
        }

        // 根据稀有度判断更换次数是否用完
        int needDust;
        boolean isRare = false;
        if (NR_RARE.contains(cardModel.getRare())) {
            if (userModel.getWeeklyDustChangeN() >= 10) {
                throw new OperationException("[%s]的每周NR更换次数已用完！", userName);
            }
            needDust = DustEnum.TO_NR.getCount();
        } else {
            isRare = true;
            if (userModel.getWeeklyDustChangeAlter() > 0) {
                throw new OperationException("[%s]的每周自选闪卡更换次数已用完！", userName);
            }
            needDust = DustEnum.TO_ALTER.getCount();
        }

        // 判断尘是否用完
        if (userModel.getDustCount() < needDust) {
            throw new OperationException("合成[%s]需要[%d]尘，当前[%s]拥有[%d]尘！", cardName, needDust, userName, userModel.getDustCount());
        }

        // 判断卡片是否已经拥有3张
        UserCardListModel cardListModel = userCardListMapper.selectByUserCard(userModel.getUserId(), cardModel.getCardPk());
        boolean newRecord = false;
        if (cardListModel == null) {
            newRecord = true;
            cardListModel = new UserCardListModel();
            cardListModel.setUserId(userModel.getUserId());
            cardListModel.setCardPk(cardModel.getCardPk());
            cardListModel.setCount(0);
        }
        if (cardListModel.getCount() >= 3) {
            throw new OperationException("[%s]当前已拥有3张[%s]，无法再合成！", userName, cardName);
        }

        // 进行转换操作
        if (isRare) {
            userModel.setWeeklyDustChangeAlter(userModel.getWeeklyDustChangeAlter() + 1);
        } else {
            userModel.setWeeklyDustChangeN(userModel.getWeeklyDustChangeN() + 1);
        }
        userModel.setDustCount(userModel.getDustCount() - needDust);
        cardListModel.setCount(cardListModel.getCount() + 1);
        if (newRecord) {
            userCardListMapper.insert(cardListModel);
        } else {
            userCardListMapper.update(cardListModel);
        }
        userDataMapper.updateByPrimaryKey(userModel);

        RollListModel rollModel = new RollListModel();
        rollModel.setRollPackageId(cardModel.getPackageId());
        rollModel.setRollUserId(userModel.getUserId());
        rollModel.setIsDisabled((byte) 0);
        if (rollListMapper.insert(rollModel) <= 0) {
            throw new OperationException("添加抽卡记录失败！");
        }
        RollDetailModel rollDetailModel = new RollDetailModel();
        rollDetailModel.setRollId(rollModel.getRollId());
        rollDetailModel.setCardPk(cardModel.getCardPk());
        rollDetailModel.setIsDust((byte) 0);
        if (rollModel.getRollId() > 0 && rollDetailMapper.insert(rollDetailModel) <= 0) {
            throw new OperationException("插入转换记录时出错！");
        }

        // 清除缓存
        RollServiceImpl.clearRollListCache();
        CardServiceImpl.clearCardListCache();
        log.warn("[{}]合成了一张[{}]", userName, cardName);

        return true;
    }

    /**
     * 将尘转换成指定卡包的随机闪
     *
     * @param packageName 卡包名
     * @param userName    用户名
     * @param password    密码
     * @return 转换结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public String dustToRare(String packageName, String userName, String password) throws Exception {
        // 玩家权限验证
        UserDataModel userModel = userDataMapper.selectByNamePassword(userName, password);
        if (userModel == null) {
            throw new OperationException("登录失败！");
        }
        if (userModel.getNonawardCount() < 100) {
            if (userModel.getDustCount() < DustEnum.TO_RANDOM.getCount()) {
                throw new OperationException("随机合成需要150尘，当前[%s]拥有[%d]尘！", userName, userModel.getDustCount());
            }
            if (userModel.getWeeklyDustChangeR() > 0) {
                throw new OperationException("[%s]本周的随机合成次数已用完！", userName);
            }
        }
        int dustCount = userModel.getDustCount() - DustEnum.TO_RANDOM.getCount();

        // 随机指定卡片
        PackageInfoModel packageModel = packageInfoMapper.selectByNameDistinct(packageName);
        if (packageModel == null) {
            throw new OperationException("找不到卡包：[%s]！", packageName);
        }
        List<PackageCardModel> rareList = packageCardMapper.selectRare(packageModel.getPackageId());
        if (rareList == null || rareList.size() == 0) {
            throw new OperationException("当前卡包没有可以抽的卡！");
        }
        Random rd = new Random();
        PackageCardModel rareCard = rareList.get(rd.nextInt(rareList.size()));

        // 记录查询
        UserCardListModel cardListModel = userCardListMapper.selectByUserCard(userModel.getUserId(), rareCard.getCardPk());
        boolean newRecord = false;
        if (cardListModel == null) {
            newRecord = true;
            cardListModel = new UserCardListModel();
            cardListModel.setUserId(userModel.getUserId());
            cardListModel.setCardPk(rareCard.getCardPk());
            cardListModel.setCount(0);
        }

        String result;
        if (cardListModel.getCount() >= 3) {
            dustCount += DustEnum.GET_RARE.getCount();
            result = String.format("你抽到了[%s](%s)，由于已达3张，直接转换为尘！", rareCard.getCardName(), rareCard.getRare());
        } else {
            cardListModel.setCount(cardListModel.getCount() + 1);
            result = String.format("你抽到了[%s](%s)！", rareCard.getCardName(), rareCard.getRare());
        }

        // 更新
        if (userModel.getNonawardCount() < 100) {
            userModel.setWeeklyDustChangeR(1);
            userModel.setDustCount(dustCount);
        } else {
            userModel.setNonawardCount(userModel.getNonawardCount() - 100);
        }
        if (userDataMapper.updateByPrimaryKey(userModel) <= 0) {
            throw new OperationException("更新用户数据错误！");
        }
        if (newRecord) {
            if (userCardListMapper.insert(cardListModel) <= 0) {
                throw new OperationException("添加卡片数据错误！");
            }
        } else {
            if (userCardListMapper.update(cardListModel) <= 0) {
                throw new OperationException("更新卡片数据错误！");
            }
        }

        RollListModel rollModel = new RollListModel();
        rollModel.setRollPackageId(rareCard.getPackageId());
        rollModel.setRollUserId(userModel.getUserId());
        rollModel.setIsDisabled((byte) 0);
        if (rollListMapper.insert(rollModel) <= 0) {
            throw new OperationException("添加抽卡记录失败！");
        }
        RollDetailModel rollDetailModel = new RollDetailModel();
        rollDetailModel.setRollId(rollModel.getRollId());
        rollDetailModel.setCardPk(rareCard.getCardPk());
        rollDetailModel.setIsDust((byte) 0);
        if (rollModel.getRollId() > 0 && rollDetailMapper.insert(rollDetailModel) <= 0) {
            throw new OperationException("插入转换记录时出错！");
        }

        // 清除缓存
        RollServiceImpl.clearRollListCache();
        CardServiceImpl.clearCardListCache();
        log.warn("[{}]在[{}]随机抽到了一张[{}]({})",
                userName,
                packageName,
                rareCard.getCardName(),
                rareCard.getRare());

        return result;
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
