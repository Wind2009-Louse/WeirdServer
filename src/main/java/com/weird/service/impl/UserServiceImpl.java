package com.weird.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.weird.mapper.main.*;
import com.weird.model.*;
import com.weird.model.dto.CardListDTO;
import com.weird.model.dto.CardSwapDTO;
import com.weird.model.dto.UserDataDTO;
import com.weird.model.enums.DustEnum;
import com.weird.model.enums.LoginTypeEnum;
import com.weird.service.RecordService;
import com.weird.service.UserService;
import com.weird.utils.BeanConverter;
import com.weird.utils.OperationException;
import com.weird.utils.PackageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.weird.utils.CacheUtil.clearCardOwnListCache;
import static com.weird.utils.CacheUtil.clearRollListWithDetailCache;

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

    @Autowired
    RecordService recordService;

    final String DEFAULT_PASSWORD = "123456";
    final String DEFAULT_PASSWORD_MD5 = "e10adc3949ba59abbe56e057f20f883e";

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
     * 根据用户名查找用户
     *
     * @param name 用户名
     * @return 用户
     */
    @Override
    public UserDataDTO getUserByName(String name) {
        return BeanConverter.convert(userDataMapper.selectByNameDistinct(name), UserDataDTO.class);
    }

    /**
     * 根据用户ID查找用户
     *
     * @param id 用户ID
     * @return 用户
     */
    @Override
    public UserDataDTO getUserById(int id) {
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
    public boolean addUser(String name, String operator) throws Exception {
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
        String hint = String.format("添加新用户：[%s]", name);
        recordService.setRecord(operator, hint);
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
        String hint = String.format("[%s]的密码发生修改", name);
        recordService.setRecord(name, hint);
        return userDataMapper.updateByPrimaryKey(model) > 0;
    }

    /**
     * 重置用户密码
     *
     * @param name 用户名
     * @return 是否更改成功
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean resetPassword(String name, String operator) throws Exception {
        UserDataModel model = userDataMapper.selectByNameDistinct(name);
        if (model == null) {
            throw new OperationException("找不到该用户名！");
        }
        if (StringUtils.equals(model.getPassword(), DEFAULT_PASSWORD_MD5)) {
            return true;
        }
        model.setPassword(DEFAULT_PASSWORD_MD5);
        String hint = String.format("[%s]的密码发生修改", name);
        recordService.setRecord(operator, hint);
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
    public String updateDust(String name, int newCount, String operator) throws Exception {
        UserDataModel model = userDataMapper.selectByNameDistinct(name);
        if (model == null) {
            throw new OperationException("找不到用户：[%s]！", name);
        }

        String hint = String.format("[%s]的尘被修改：%d->%d", name, model.getDustCount(), newCount);

        model.setDustCount(newCount);
        int updateCount = userDataMapper.updateByPrimaryKey(model);
        if (updateCount > 0) {
            recordService.setRecord(operator, hint);
            return hint;
        } else {
            throw new OperationException("修改失败！");
        }
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
        if (packageModel == null || PackageUtil.canNotRoll(packageModel.getPackageName())) {
            throw new OperationException("无法合成[%s]！", cardName);
        }

        List<CardListDTO> records = userCardListMapper.selectCardListUser(null, null, null, userName, cardModel.getCardPk());
        if (records.size() <= 0) {
            throw new OperationException("找不到卡片：[%s]！", cardName);
        }

        // 根据稀有度判断更换次数是否用完
        int needDust;
        boolean isRare = false;
        if (PackageUtil.NR_LIST.contains(cardModel.getRare())) {
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
        if (cardListModel.getCount() >= 3 && PackageUtil.NR_LIST.contains(cardModel.getRare())) {
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
        clearRollListWithDetailCache();
        clearCardOwnListCache();
        recordService.setRecord(userName, "[%s]合成了一张[%s]，剩余尘：%d。", userName, cardName, userModel.getDustCount());

        return true;
    }

    /**
     * 将尘转换成指定卡包的随机闪
     *
     * @param packageName 卡包名
     * @param userName    用户名
     * @param password    密码
     * @param dustFirst   优先使用尘
     * @return 转换结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public String dustToRare(String packageName, String userName, String password, int dustFirst) throws Exception {
        // 玩家权限验证
        UserDataModel userModel = userDataMapper.selectByNamePassword(userName, password);
        if (userModel == null) {
            throw new OperationException("登录失败！");
        }

        int dustCount = userModel.getDustCount();
        if (userModel.getNonawardCount() < 100) {
            if (userModel.getDustCount() < DustEnum.TO_RANDOM.getCount()) {
                throw new OperationException("随机合成需要150尘，当前[%s]拥有[%d]尘！", userName, dustCount);
            }
            if (userModel.getWeeklyDustChangeR() > 0) {
                throw new OperationException("[%s]本周的随机合成次数已用完！", userName);
            }
        }

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

        cardListModel.setCount(cardListModel.getCount() + 1);
        String result = String.format("抽到了[%s](%s)", rareCard.getCardName(), rareCard.getRare());
        String resultToPlayer = String.format("你%s！", result);

        // 更新
        dustCount -= DustEnum.TO_RANDOM.getCount();
        if (dustFirst > 0) {
            if (dustCount >= 0) {
                userModel.setDustCount(dustCount);
                userModel.setWeeklyDustChangeR(1);
                recordService.setRecord(userName,"[%s]使用150尘roll闪，剩余尘：%d。", userName, dustCount);
            } else {
                userModel.setNonawardCount(userModel.getNonawardCount() - 100);
                recordService.setRecord(userName,"[%s]使用月见黑roll闪，剩余月见黑：%d。", userName, userModel.getNonawardCount());
            }
        } else {
            if (userModel.getNonawardCount() >= 100) {
                userModel.setNonawardCount(userModel.getNonawardCount() - 100);
                recordService.setRecord(userName,"[%s]使用月见黑roll闪，剩余月见黑：%d。", userName, userModel.getNonawardCount());
            } else {
                userModel.setDustCount(dustCount);
                userModel.setWeeklyDustChangeR(1);
                recordService.setRecord(userName,"[%s]使用150尘roll闪，剩余尘：%d。", userName, dustCount);
            }
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
        clearRollListWithDetailCache();
        clearCardOwnListCache();
        recordService.setRecord(userName,"[%s]在[%s]随机抽到了一张[%s](%s)",
                userName,
                packageName,
                rareCard.getCardName(),
                rareCard.getRare());

        return resultToPlayer;
    }

    /**
     * 将多余的闪卡换成尘
     *
     * @param userName 用户名
     * @param password 密码
     * @param cardName 卡片名称
     * @param count    转换的数量
     * @return 是否转换成功
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public int rareToDust(String userName, String password, String cardName, int count) throws Exception {
        UserDataModel userModel = userDataMapper.selectByNamePassword(userName, password);
        if (userModel == null) {
            throw new OperationException("登录失败！");
        }

        PackageCardModel cardModel = packageCardMapper.selectByNameDistinct(cardName);
        if (cardModel == null) {
            throw new OperationException("找不到卡片：[%s]！", cardName);
        }
        if (PackageUtil.NR_LIST.contains(cardModel.getRare())) {
            throw new OperationException("NR卡无法分解！");
        }

        UserCardListModel cardListModel = userCardListMapper.selectByUserCard(userModel.getUserId(), cardModel.getCardPk());
        if (cardListModel == null || (cardListModel.getCount() - count < 3)) {
            throw new OperationException("拥有的[%s]不足以分解！", cardName);
        }
        int newDustCount = count * DustEnum.GET_RARE.getCount();
        cardListModel.setCount(cardListModel.getCount() - count);
        userModel.setDustCount(userModel.getDustCount() + newDustCount);

        userDataMapper.updateByPrimaryKey(userModel);
        userCardListMapper.update(cardListModel);
        clearCardOwnListCache();
        recordService.setRecord(userName,"[%s]分解了%d张[%s]，当前尘：%d。", userName, count, cardName, userModel.getDustCount());
        return newDustCount;
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
    public String updateAward(String name, int newCount, String operator) throws Exception {
        UserDataModel model = userDataMapper.selectByNameDistinct(name);
        if (model == null) {
            throw new OperationException("找不到用户：[%s]！", name);
        }

        String successHint = String.format("%s的月见黑被修改：%d->%d", name, model.getNonawardCount(), newCount);
        model.setNonawardCount(newCount);
        int updateCount = userDataMapper.updateByPrimaryKey(model);
        if (updateCount > 0) {
            recordService.setRecord(operator, successHint);
            return successHint;
        } else {
            throw new OperationException("月见黑修改失败！");
        }
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
    public String updateDuelPoint(String name, int newCount, String operator) throws Exception {
        UserDataModel model = userDataMapper.selectByNameDistinct(name);
        if (model == null) {
            throw new OperationException("找不到用户：[%s]！", name);
        }

        String successHint = String.format("%s的月见黑被修改：%d->%d", name, model.getDuelPoint(), newCount);
        model.setDuelPoint(newCount);
        int updateCount = userDataMapper.updateByPrimaryKey(model);
        if (updateCount > 0) {
            recordService.setRecord(operator, successHint);
            return successHint;
        } else {
            throw new OperationException("修改失败！");
        }
    }

    /**
     * 交换两个用户持有的卡片
     *
     * @param dto 参数
     * @return 是否交换成功
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public String swapCard(CardSwapDTO dto, String operator) throws Exception {
        // 判断用户是否存在
        UserDataModel userA = userDataMapper.selectByNameDistinct(dto.getUserA());
        if (userA == null) {
            throw new OperationException("找不到用户：[%s]！", dto.getUserA());
        }
        UserDataModel userB = userDataMapper.selectByNameDistinct(dto.getUserB());
        if (userB == null) {
            throw new OperationException("找不到用户：[%s]！", dto.getUserB());
        }

        // 判断卡片是否存在
        PackageCardModel cardA = packageCardMapper.selectByNameDistinct(dto.getCardA());
        if (cardA == null) {
            throw new OperationException("找不到该卡片：[%s]！", dto.getCardA());
        }
        PackageCardModel cardB = packageCardMapper.selectByNameDistinct(dto.getCardB());
        if (cardB == null) {
            throw new OperationException("找不到该卡片：[%s]！", dto.getCardB());
        }
        boolean cardRareA = PackageUtil.NR_LIST.contains(cardA.getRare());
        boolean cardRareB = PackageUtil.NR_LIST.contains(cardB.getRare());
        if (cardRareA != cardRareB) {
            throw new OperationException("两张卡片的稀有程度不同，无法交换！");
        }

        // 判断用户是否持有对应卡片
        UserCardListModel userACardA = userCardListMapper.selectByUserCard(userA.getUserId(), cardA.getCardPk());
        if (userACardA == null || userACardA.getCount() <= 0) {
            throw new OperationException("用户[%s]不拥有[%s]！", dto.getUserA(), dto.getCardA());
        }
        UserCardListModel userBCardB = userCardListMapper.selectByUserCard(userB.getUserId(), cardB.getCardPk());
        if (userBCardB == null || userBCardB.getCount() <= 0) {
            throw new OperationException("用户[%s]不拥有[%s]！", dto.getUserB(), dto.getCardB());
        }

        // 交换
        userACardA.setCount(userACardA.getCount() - 1);
        userBCardB.setCount(userBCardB.getCount() - 1);

        UserCardListModel userACardB = userCardListMapper.selectByUserCard(userA.getUserId(), cardB.getCardPk());
        boolean existA = true;
        if (userACardB == null) {
            existA = false;
            userACardB = new UserCardListModel();
            userACardB.setUserId(userA.getUserId());
            userACardB.setCardPk(cardB.getCardPk());
            userACardB.setCount(0);
        }
        userACardB.setCount(userACardB.getCount() + 1);

        UserCardListModel userBCardA = userCardListMapper.selectByUserCard(userB.getUserId(), cardA.getCardPk());
        boolean existB = true;
        if (userBCardA == null) {
            existB = false;
            userBCardA = new UserCardListModel();
            userBCardA.setUserId(userB.getUserId());
            userBCardA.setCardPk(cardA.getCardPk());
            userBCardA.setCount(0);
        }
        userBCardA.setCount(userBCardA.getCount() + 1);

        if (userCardListMapper.update(userACardA) <= 0
                || userCardListMapper.update(userBCardB) <= 0) {
            throw new OperationException("交换数量更新失败！");
        }
        if (existA) {
            if (userCardListMapper.update(userACardB) <= 0) {
                throw new OperationException("交换数量更新失败！");
            }
        } else {
            if (userCardListMapper.insert(userACardB) <= 0) {
                throw new OperationException("交换数量添加失败！");
            }
        }
        if (existB) {
            if (userCardListMapper.update(userBCardA) <= 0) {
                throw new OperationException("交换数量更新失败！");
            }
        } else {
            if (userCardListMapper.insert(userBCardA) <= 0) {
                throw new OperationException("交换数量添加失败！");
            }
        }

        clearCardOwnListCache();
        recordService.setRecord(operator, "[%s]的[%s]、[%s]的[%s]进行交换", dto.getUserA(), dto.getCardA(), dto.getUserB(), dto.getCardB());
        return "交换成功！";
    }
}
