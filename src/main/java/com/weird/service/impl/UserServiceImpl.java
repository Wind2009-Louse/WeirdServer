package com.weird.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.weird.config.AutoConfig;
import com.weird.config.WeirdConfig;
import com.weird.facade.BroadcastFacade;
import com.weird.facade.RecordFacade;
import com.weird.mapper.main.*;
import com.weird.model.*;
import com.weird.model.dto.CardSwapDTO;
import com.weird.model.dto.UserDataDTO;
import com.weird.model.dto.UserSessionDTO;
import com.weird.model.enums.DustEnum;
import com.weird.model.enums.LoginTypeEnum;
import com.weird.model.param.ReplaceCardParam;
import com.weird.service.UserService;
import com.weird.utils.BeanConverter;
import com.weird.utils.OperationException;
import com.weird.utils.PackageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.weird.utils.CacheUtil.*;

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
    RecordFacade recordFacade;

    @Autowired
    BroadcastFacade broadcastFacade;

    final static String DEFAULT_PASSWORD = "123456";
    final static String DEFAULT_PASSWORD_MD5 = "e10adc3949ba59abbe56e057f20f883e";

    final static int ROLL_BY_NONE  = 0;
    final static int ROLL_BY_DUST  = 1;
    final static int ROLL_BY_AWARD = 2;
    final static int ROLL_BY_CARD  = 3;

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
     * 根据用户名和密码检查，获得登录凭证
     *
     * @param name              用户名
     * @param encryptedPassword 密码
     * @return 登录凭证
     */
    @Override
    public UserSessionDTO checkLoginAndGetSession(String name, String encryptedPassword) {
        UserSessionDTO session = new UserSessionDTO();
        UserDataModel model = userDataMapper.selectByNamePassword(name, encryptedPassword);
        if (model != null) {
            session.setUserName(name);
            if (model.getIsAdmin() == 1) {
                session.setType(LoginTypeEnum.ADMIN);
            } else {
                session.setType(LoginTypeEnum.NORMAL);
            }
            session.setExpireTimestamp(System.currentTimeMillis() + UserSessionExpireTime);
            String key = DigestUtils.md5DigestAsHex(session.toString().getBytes()).substring(0, 10);
            session.setSession(key);

            putUserSessionCache(session);
        }

        return session;
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
        UserDataModel modelList = userDataMapper.selectByNameInAllDistinct(name);
        if (modelList != null) {
            throw new OperationException("该用户：[%s]已存在！", name);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        boolean isWeekend = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;

        UserDataModel newModel = new UserDataModel();
        newModel.setUserName(name);
        newModel.setPassword(DEFAULT_PASSWORD_MD5);
        newModel.setIsAdmin((byte) 0);
        newModel.setDustCount(0);
        newModel.setDuelPoint(0);
        newModel.setNonawardCount(0);
        newModel.setRoulette(2);
        newModel.setQq("");
        if (isWeekend) {
            newModel.setDoubleRareCount(10);
        }
        userDataMapper.insert(newModel);
        String hint = String.format("添加新用户：[%s]", name);
        recordFacade.setRecord(operator, hint);
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
        model.setQq("");
        String hint = String.format("[%s]的密码发生修改", name);
        recordFacade.setRecord(name, hint);
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
        model.setQq("");
        String hint = String.format("[%s]的密码发生修改", name);
        recordFacade.setRecord(operator, hint);
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
    public String updateDust(String name, int newCount, String operator) throws OperationException {
        UserDataModel model = userDataMapper.selectByNameDistinct(name);
        if (model == null) {
            throw new OperationException("找不到用户：[%s]！", name);
        }

        String hint = String.format("[%s]的尘被修改：%d->%d", name, model.getDustCount(), newCount);

        model.setDustCount(newCount);
        int updateCount = userDataMapper.updateByPrimaryKey(model);
        if (updateCount > 0) {
            recordFacade.setRecord(operator, hint);
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
        if (packageModel == null) {
            throw new OperationException("无法合成[%s]！", cardName);
        }
        boolean onlyCoin = PackageUtil.canNotRoll(packageModel.getPackageName());

        List<Integer> visibleCardPkList = userCardListMapper.getVisibleCardPkList();
        if (!visibleCardPkList.contains(cardModel.getCardPk())) {
            throw new OperationException("找不到卡片：[%s]！", cardName);
        }

        // 根据稀有度判断更换次数是否用完
        int needDust = 0;
        int needCoin = 0;
        boolean isRare = false;
        if (cardModel.getNeedCoin() > 0) {
            needCoin = cardModel.getNeedCoin();
        } else if (PackageUtil.NR_LIST.contains(cardModel.getRare())) {
            needDust = DustEnum.TO_NR.getCount();
        } else {
            isRare = true;
            if (userModel.getWeeklyDustChangeAlter() > 0) {
                throw new OperationException("[%s]的每周自选闪卡更换次数已用完！", userName);
            }
            needDust = DustEnum.TO_ALTER.getCount();
        }

        if ((onlyCoin && needCoin == 0) || PackageUtil.onlyByRoll(cardName)) {
            throw new OperationException("无法合成[%s]！", cardName);
        }

        // 判断尘/硬币是否用完
        if (needDust > 0 && userModel.getDustCount() < needDust) {
            throw new OperationException("合成[%s]需要[%d]尘，当前[%s]拥有[%d]尘！", cardName, needDust, userName, userModel.getDustCount());
        }
        if (needCoin > 0 && userModel.getCoin() < needCoin) {
            throw new OperationException("合成[%s]需要[%d]硬币，当前[%s]拥有[%d]枚硬币！", cardName, needCoin, userName, userModel.getCoin());
        }

        // 判断卡片是否超额
        UserCardListModel cardListModel = userCardListMapper.selectByUserCard(userModel.getUserId(), cardModel.getCardPk());
        boolean newRecord = false;
        if (cardListModel == null) {
            newRecord = true;
            cardListModel = new UserCardListModel();
            cardListModel.setUserId(userModel.getUserId());
            cardListModel.setCardPk(cardModel.getCardPk());
            cardListModel.setCount(0);
        }
        int cardMaxCount = 3;
        if (onlyCoin) {
            cardMaxCount = 1;
        }
        if (cardListModel.getCount() >= cardMaxCount) {
            throw new OperationException("[%s]当前已拥有%d张[%s]，无法再合成！", userName, cardMaxCount, cardName);
        }

        // 进行转换操作
        if (!onlyCoin) {
            if (isRare) {
                userModel.setWeeklyDustChangeAlter(userModel.getWeeklyDustChangeAlter() + 1);
            } else {
                userModel.setWeeklyDustChangeN(userModel.getWeeklyDustChangeN() + 1);
            }
        }
        userModel.setDustCount(userModel.getDustCount() - needDust);
        userModel.setCoin(userModel.getCoin() - needCoin);
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
        rollDetailModel.setCardName(cardModel.getCardName());
        rollDetailModel.setRare(cardModel.getRare());
        if (rollModel.getRollId() > 0 && rollDetailMapper.insert(rollDetailModel) <= 0) {
            throw new OperationException("插入转换记录时出错！");
        }

        // 清除缓存
        clearRollListWithDetailCache();
        clearCardOwnListCache();
        if (onlyCoin) {
            recordFacade.setRecord(userName, "[%s]合成了一张[%s]，剩余硬币：%d。", userName, cardName, userModel.getCoin());
        } else {
            recordFacade.setRecord(userName, "[%s]合成了一张[%s]，剩余尘：%d。", userName, cardName, userModel.getDustCount());
        }

        if (isRare || onlyCoin) {
            try {
                int ownCount = userCardListMapper.selectCardOwnCount(cardModel.getCardPk());
                int selfOwnCount = userCardListMapper.selectCardOwnCountByUser(cardModel.getCardPk(), userModel.getUserId());

                if (isRare) {
                    broadcastFacade.sendMsgAsync(
                            String.format(
                                    "【指定合成】%s 使用300尘合成了第%d/%d张[%s]%s，实力进一步提升！",
                                    userName, selfOwnCount, ownCount, cardModel.getRare(), cardName)
                    );
                } else {
                    broadcastFacade.sendMsgAsync(
                            String.format(
                                    "【指定合成】%s 使用%d硬币兑换了第%d/%d张[%s]%s，实力进一步提升！",
                                    userName, needCoin, selfOwnCount, ownCount, cardModel.getRare(), cardName)
                    );
                }
            } catch (Exception e) {
                log.error("广播失败：", e);
            }
        }

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
        int nonAwardCount = userModel.getNonawardCount();


        int rollWay = ROLL_BY_NONE;
        String lastException = "未知错误，选择随机合成方式失败。";
        String awardConditionName = AutoConfig.fetchAwardCard();
        UserCardListModel cutoffModel = null;
        List<Integer> rollWayCheckList;
        if (dustFirst > 0) {
            rollWayCheckList = Arrays.asList(ROLL_BY_DUST, ROLL_BY_CARD, ROLL_BY_AWARD);
        } else {
            rollWayCheckList = Arrays.asList(ROLL_BY_CARD, ROLL_BY_AWARD, ROLL_BY_DUST);
        }
        for (int checkWay : rollWayCheckList) {
            switch (checkWay) {
                case ROLL_BY_DUST:
                    if (userModel.getDustCount() < DustEnum.TO_RANDOM.getCount()) {
                        lastException = String.format("随机合成需要150尘，当前[%s]拥有[%d]尘！", userName, dustCount);
                    } else if (userModel.getWeeklyDustChangeR() > 0) {
                        lastException = String.format("[%s]本周的随机合成次数已用完！", userName);
                    } else {
                        rollWay = ROLL_BY_DUST;
                    }
                    break;
                case ROLL_BY_AWARD:
                    if (nonAwardCount >= WeirdConfig.fetchNonAwardLimit()) {
                        rollWay = ROLL_BY_AWARD;
                    }
                    break;
                case ROLL_BY_CARD:
                    PackageCardModel awardConditionCard = packageCardMapper.selectByNameDistinct(awardConditionName);
                    if (awardConditionCard != null) {
                        cutoffModel = userCardListMapper.selectByUserCard(userModel.getUserId(), awardConditionCard.getCardPk());
                        if (cutoffModel != null && cutoffModel.getCount() > 0) {
                            cutoffModel.setCount(cutoffModel.getCount() - 1);
                            rollWay = ROLL_BY_CARD;
                        }
                    }
                    break;
                default:
                    throw new OperationException("非法操作！");
            }
            if (rollWay != ROLL_BY_NONE) {
                break;
            }
        }
        if (rollWay == ROLL_BY_NONE) {
            throw new OperationException(lastException);
        }

        // 随机指定卡片
        PackageInfoModel packageModel = packageInfoMapper.selectByNameDistinct(packageName);
        if (packageModel == null) {
            throw new OperationException("找不到卡包：[%s]！", packageName);
        }
        List<PackageCardModel> rareList = packageCardMapper.selectRare(packageModel.getPackageId());
        if (rareList != null) {
            rareList = rareList.stream().filter(c -> !PackageUtil.onlyByRoll(c.getCardName())).collect(Collectors.toList());
        }
        if (rareList == null || rareList.size() == 0) {
            throw new OperationException("当前卡包没有可以抽的卡！");
        }
        Random rd = new Random();
        PackageCardModel rareCard = rareList.get(rd.nextInt(rareList.size()));
        String cardName = rareCard.getCardName();

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
        String result = String.format("抽到了[%s](%s)", cardName, rareCard.getRare());
        String resultToPlayer = String.format("你%s！", result);

        // 更新
        dustCount -= DustEnum.TO_RANDOM.getCount();
        switch (rollWay) {
            case ROLL_BY_DUST:
                userModel.setDustCount(dustCount);
                userModel.setWeeklyDustChangeR(1);
                recordFacade.setRecord(userName, "[%s]使用150尘roll闪，剩余尘：%d。", userName, dustCount);
                break;
            case ROLL_BY_AWARD:
                userModel.setNonawardCount(nonAwardCount - WeirdConfig.fetchNonAwardLimit());
                recordFacade.setRecord(userName, "[%s]使用月见黑roll闪，剩余月见黑：%d。", userName, nonAwardCount);
                break;
            case ROLL_BY_CARD:
                if (userCardListMapper.update(cutoffModel) > 0) {
                    recordFacade.setRecord(userName, "[%s]使用[%s]roll闪，剩余数量：%d。", userName, awardConditionName, cutoffModel.getCount());
                } else {
                    throw new OperationException("扣除[%s]失败！", awardConditionName);
                }
                break;
            default:
                throw new OperationException("扣除抽卡费用失败。");
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
        rollDetailModel.setCardName(cardName);
        rollDetailModel.setRare(rareCard.getRare());
        if (rollModel.getRollId() > 0 && rollDetailMapper.insert(rollDetailModel) <= 0) {
            throw new OperationException("插入转换记录时出错！");
        }

        // 清除缓存
        clearRollListWithDetailCache();
        clearCardOwnListCache();
        recordFacade.setRecord(userName, "[%s]在[%s]随机抽到了一张[%s](%s)",
                userName,
                packageName,
                cardName,
                rareCard.getRare());

        try {
            int ownCount = userCardListMapper.selectCardOwnCount(rareCard.getCardPk());
            int selfOwnCount = userCardListMapper.selectCardOwnCountByUser(rareCard.getCardPk(), userModel.getUserId());
            broadcastFacade.sendMsgAsync(
                    String.format(
                            "【随机抽取】加美希尔表示震惊！%s 随机抽到了第%d/%d张[%s]%s！",
                            userName, selfOwnCount, ownCount, rareCard.getRare(), cardName)
            );
        } catch (Exception e) {
            log.error("广播失败：", e);
        }

        return resultToPlayer;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public String coinToCard(String cardName, String userName, String password) throws Exception {
        // 玩家权限验证
        UserDataModel userModel = userDataMapper.selectByNamePassword(userName, password);
        if (userModel == null) {
            throw new OperationException("登录失败！");
        }

        // 判断卡片是否可兑换
        PackageCardModel cardModel = packageCardMapper.selectByNameDistinct(cardName);
        if (cardModel == null) {
            throw new OperationException("找不到卡片：[%s]！", cardName);
        }
        int needCoin = cardModel.getNeedCoin();
        if (needCoin <= 0) {
            throw new OperationException("该卡片无法用硬币兑换：[%s]！", cardName);
        }
        int remainCoin = userModel.getCoin() - needCoin;
        if (remainCoin < 0) {
            throw new OperationException("兑换[%s]需要[%d]枚硬币，当前[%s]拥有[%d]枚硬币！", cardName, needCoin, userName, userModel.getCoin());
        }
        UserCardListModel cardListModel = userCardListMapper.selectByUserCard(userModel.getUserId(), cardModel.getCardPk());
        if (cardListModel != null && cardListModel.getCount() > 0) {
            throw new OperationException("你已拥有[%s]，无法重复兑换！", cardName);
        }

        userModel.setCoin(remainCoin);
        if (userDataMapper.updateByPrimaryKeySelective(userModel) > 0) {
            String recordResult = String.format("[%s]用[%d]枚硬币兑换了1张[%s]", userModel.getUserName(), needCoin, cardModel.getCardName());
            int result = 0;
            if (cardListModel == null) {
                cardListModel = new UserCardListModel();
                cardListModel.setUserId(userModel.getUserId());
                cardListModel.setCardPk(cardModel.getCardPk());
                cardListModel.setCount(1);
                result = userCardListMapper.insert(cardListModel);
            } else {
                cardListModel.setCount(1);
                result = userCardListMapper.update(cardListModel);
            }
            if (result > 0) {
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
                rollDetailModel.setCardName(cardModel.getCardName());
                rollDetailModel.setRare(cardModel.getRare());
                if (rollModel.getRollId() > 0 && rollDetailMapper.insert(rollDetailModel) <= 0) {
                    throw new OperationException("插入转换记录时出错！");
                }
                // 清除缓存
                clearRollListWithDetailCache();
                clearCardOwnListCache();

                recordFacade.setRecord(userName, recordResult);

                try {
                    int ownCount = userCardListMapper.selectCardOwnCount(cardModel.getCardPk());
                    int selfOwnCount = userCardListMapper.selectCardOwnCountByUser(cardModel.getCardPk(), userModel.getUserId());
                    broadcastFacade.sendMsgAsync(
                            String.format(
                                    "【指定合成】%s 使用%d硬币兑换了第%d/%d张[%s]%s，实力进一步提升！",
                                    userName, needCoin, selfOwnCount, ownCount, cardModel.getRare(), cardName)
                    );
                } catch (Exception e) {
                    log.error("广播失败：", e);
                }


                return "兑换成功！";
            }
        }
        throw new OperationException("用户信息更新失败");
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

        if (PackageUtil.onlyByRoll(cardName)) {
            throw new OperationException("特殊卡片无法分解！");
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
        recordFacade.setRecord(userName, "[%s]分解了%d张[%s]，当前尘：%d。", userName, count, cardName, userModel.getDustCount());
        broadcastFacade.sendMsgAsync(
                String.format(
                        "【分解】壕无人性！%s分解了自己的%d张[%s]%s，换取了%d尘！！",
                        userName, count, cardModel.getRare(), cardName, newDustCount)
        );
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
    public String updateAward(String name, int newCount, String operator) throws OperationException {
        UserDataModel model = userDataMapper.selectByNameDistinct(name);
        if (model == null) {
            throw new OperationException("找不到用户：[%s]！", name);
        }

        String successHint = String.format("%s的月见黑被修改：%d->%d", name, model.getNonawardCount(), newCount);
        model.setNonawardCount(newCount);
        int updateCount = userDataMapper.updateByPrimaryKey(model);
        if (updateCount > 0) {
            recordFacade.setRecord(operator, successHint);
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
    public String updateDuelPoint(String name, int newCount, String operator) throws OperationException {
        UserDataModel model = userDataMapper.selectByNameDistinct(name);
        if (model == null) {
            throw new OperationException("找不到用户：[%s]！", name);
        }

        String successHint = String.format("%s的DP被修改：%d->%d", name, model.getDuelPoint(), newCount);
        model.setDuelPoint(newCount);
        int updateCount = userDataMapper.updateByPrimaryKey(model);
        if (updateCount > 0) {
            recordFacade.setRecord(operator, successHint);
            return successHint;
        } else {
            throw new OperationException("修改失败！");
        }
    }
    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public int decDuelPoint(String name, int count, String operator) throws OperationException {
        UserDataModel model = userDataMapper.selectByNameDistinct(name);
        if (model == null) {
            throw new OperationException("找不到用户：[%s]！", name);
        }
        int newCount = model.getDuelPoint() - count;

        String successHint = String.format("%s的DP被修改：%d->%d", name, model.getDuelPoint(), newCount);
        model.setDuelPoint(newCount);
        int updateCount = userDataMapper.updateByPrimaryKey(model);
        if (updateCount > 0) {
            recordFacade.setRecord(operator, successHint);
            return newCount;
        } else {
            throw new OperationException("修改失败！");
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public String updateCoin(String name, int newCount, String operator) throws OperationException {
        UserDataModel model = userDataMapper.selectByNameDistinct(name);
        if (model == null) {
            throw new OperationException("找不到用户：[%s]！", name);
        }

        String successHint = String.format("%s的硬币被修改：%d->%d", name, model.getCoin(), newCount);
        model.setCoin(newCount);
        int updateCount = userDataMapper.updateByPrimaryKey(model);
        if (updateCount > 0) {
            recordFacade.setRecord(operator, successHint);
            return successHint;
        } else {
            throw new OperationException("修改失败！");
        }
    }

    @Override
    public String updateRoulette(String name, int newCount, String operator) throws OperationException {
        UserDataModel model = userDataMapper.selectByNameDistinct(name);
        if (model == null) {
            throw new OperationException("找不到用户：[%s]！", name);
        }

        String successHint = String.format("%s的转盘次数被修改：%d->%d", name, model.getRoulette(), newCount);
        model.setRoulette(newCount);
        int updateCount = userDataMapper.updateByPrimaryKey(model);
        if (updateCount > 0) {
            recordFacade.setRecord(operator, successHint);
            return successHint;
        } else {
            throw new OperationException("修改失败！");
        }
    }

    @Override
    public String updateRollCount(String name, int newCount, String operator) throws OperationException {
        UserDataModel model = userDataMapper.selectByNameDistinct(name);
        if (model == null) {
            throw new OperationException("找不到用户：[%s]！", name);
        }

        String successHint = String.format("%s的抽卡计数被修改：%d->%d", name, model.getRollCount(), newCount);
        model.setRollCount(newCount);
        int updateCount = userDataMapper.updateByPrimaryKey(model);
        if (updateCount > 0) {
            recordFacade.setRecord(operator, successHint);
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
    public String swapCard(CardSwapDTO dto, String operator) throws OperationException {
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
        recordFacade.setRecord(operator, "[%s]的[%s]、[%s]的[%s]进行交换", dto.getUserA(), dto.getCardA(), dto.getUserB(), dto.getCardB());
        return "交换成功！";
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public String exchangeOwnCard(ReplaceCardParam param) throws Exception {
        // 判断用户是否存在
        String targetUserName = param.getTargetUser();
        UserDataModel targetUser = userDataMapper.selectByNameDistinct(targetUserName);
        if (targetUser == null) {
            throw new OperationException("找不到用户：[%s]！", targetUserName);
        }

        // 判断卡片是否存在
        PackageCardModel cardA = packageCardMapper.selectByNameDistinct(param.getOldCardName());
        if (cardA == null) {
            throw new OperationException("找不到该卡片：[%s]！", param.getOldCardName());
        }
        PackageCardModel cardB = packageCardMapper.selectByNameDistinct(param.getNewCardName());
        if (cardB == null) {
            throw new OperationException("找不到该卡片：[%s]！", param.getNewCardName());
        }

        int updateResult = 0;

        // 旧数据中减除卡片数量
        UserCardListModel originCard = userCardListMapper.selectByUserCard(targetUser.getUserId(), cardA.getCardPk());
        if (originCard == null) {
            originCard = new UserCardListModel();
        }
        int oldCardOriginCount = originCard.getCount();
        int oldCardCurrentCount = originCard.getCount() - param.getCount();
        if (oldCardCurrentCount < 0) {
            throw new OperationException("[%s]的持有数量为[%d]张，不满[%d]张！", param.getOldCardName(), originCard.getCount(), param.getCount());
        }
        originCard.setCount(oldCardCurrentCount);
        updateResult = userCardListMapper.update(originCard);
        if (updateResult <= 0) {
            throw new OperationException("修改旧卡片数量失败！");
        }

        // 新数据中添加卡片数量
        UserCardListModel nextCard = userCardListMapper.selectByUserCard(targetUser.getUserId(), cardB.getCardPk());
        int nextCardOriginCount = 0;
        int nextCardCurrentCount;
        if (nextCard == null) {
            nextCardCurrentCount = param.getCount();

            nextCard = new UserCardListModel();
            nextCard.setUserId(targetUser.getUserId());
            nextCard.setCardPk(cardB.getCardPk());
            nextCard.setCount(nextCardCurrentCount);
            updateResult = userCardListMapper.insert(nextCard);
        } else {
            nextCardOriginCount = nextCard.getCount();
            nextCardCurrentCount = nextCard.getCount() + param.getCount();
            nextCard.setCount(nextCardCurrentCount);
            updateResult = userCardListMapper.update(nextCard);
        }
        if (updateResult <= 0) {
            throw new OperationException("修改新卡片数量失败！");
        }

        // 报告结果
        clearCardOwnListCache();
        String result = String.format("[%s]的卡被替换：[%s](%d->%d), [%s](%d->%d)",
                targetUserName,
                param.getOldCardName(), oldCardOriginCount, oldCardCurrentCount,
                param.getNewCardName(), nextCardOriginCount, nextCardCurrentCount);

        recordFacade.setRecord(param.getName(), result);
        return result;
    }

    @Override
    public UserDataDTO getUserByQQ(String qq) {
        return BeanConverter.convert(userDataMapper.selectByQQInAllDistinct(qq), UserDataDTO.class);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean updateQQ(String name, String qq) throws OperationException {
        UserDataModel model = userDataMapper.selectByNameInAllDistinct(name);
        if (model == null) {
            throw new OperationException("找不到该用户名！");
        }
        model.setQq(qq);

        userDataMapper.clearQQ(qq);

        String hint = String.format("[%s]的绑定QQ修改为%s", name, qq);
        recordFacade.setRecord("system", hint);
        return userDataMapper.updateByPrimaryKey(model) > 0;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean unbindQQ(String qq) {
        return userDataMapper.clearQQ(qq) > 0;
    }

    @Override
    public boolean adminCheck(String userName) {
        UserDataModel model = userDataMapper.selectByNameInAllDistinct(userName);
        return (model != null && model.getIsAdmin() > 0);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean updateDisabled(String target, String operator, int disabled) {
        int count = userDataMapper.updateDisabled(target, disabled);
        if (count > 0) {
            recordFacade.setRecord(operator, "[%s]的禁用状态被修改为%d", target, disabled);
            return true;
        }
        return false;
    }

    @Override
    public List<String> showDisabledUserName() {
        return userDataMapper.showDisabled();
    }

    @Override
    public int getDoubleRareCount(String userName) {
        return userDataMapper.selectDoubleRareCount(userName);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean updateDoubleRareCount(String userName, int count, String operator) {
        int originCount = userDataMapper.selectDoubleRareCount(userName);
        if (originCount != count) {
            int result = userDataMapper.updateDoubleRareCount(userName, count);
            if (result <= 0) {
                return false;
            }
            recordFacade.setRecord(operator, "[%s]的百八数量更新：%d->%d", userName, originCount, count);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean resetDoubleRareCount(int count) {
        return userDataMapper.resetDoubleRareCount(count) > 0;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public boolean handleDuelResult(long duelId, String userName, int winCount, int lostCount, int winDp, int lostDp) {
        boolean result = false;
        UserDataModel userModel = userDataMapper.selectByNameDistinct(userName);
        if (userModel == null || winCount + lostCount == 0) {
            return result;
        }
        int oldWinCount = userModel.getDailyWin();
        int oldLostCount = userModel.getDailyLost();
        int newWinCount = oldWinCount + winCount;
        int newLostCount = oldLostCount + lostCount;
        int oldDp = userModel.getDuelPoint();
        int missionCount = userModel.getWeeklyMission();

        int increaseDp = winCount * winDp + lostCount * lostDp;
        boolean triggerWin = (!isDailyWinEnough(oldWinCount, oldLostCount) && isDailyWinEnough(newWinCount, newLostCount));
        boolean triggerLost = (!isDailyLostEnough(oldLostCount) && isDailyLostEnough(newLostCount));
        int newDp = oldDp + increaseDp + (triggerWin ? 10 : 0) + (triggerLost ? 5 : 0);

        StringBuilder sb = new StringBuilder();
        sb.append(userName).append(":").append("决斗ID:").append(duelId).append(",比分=").append(winCount).append(":").append(lostCount).append(",DP=").append(oldDp).append("+").append(increaseDp);
        if (triggerWin) {
            sb.append("+10");
            missionCount ++;
            if (missionCount == AutoConfig.fetchWeeklyMissionToExchange()) {
                result = true;
            }
        }
        if (triggerLost) {
            sb.append("+5");
        }
        sb.append("=").append(newDp);

        UserDataModel updateModel = new UserDataModel();
        updateModel.setUserId(userModel.getUserId());
        updateModel.setWeeklyMission(missionCount);
        updateModel.setDailyWin(newWinCount);
        updateModel.setDailyLost(newLostCount);
        updateModel.setDuelPoint(newDp);
        if (userDataMapper.updateByPrimaryKeySelective(updateModel) > 0) {
            recordFacade.setRecord(userName, sb.toString());
        }
        return result;
    }

    private boolean isDailyWinEnough(int winCount, int lostCount) {
        return (winCount >= 3 || (winCount + lostCount >= 6));
    }

    private boolean isDailyLostEnough(int lostCount) {
        return (lostCount >= 3);
    }

    @Override
    public int getUserOwnCardCount(String userName, String cardName) throws OperationException {
        if (StringUtils.isEmpty(cardName)) {
            return 0;
        }
        UserDataModel targetUser = userDataMapper.selectByNameDistinct(userName);
        if (targetUser == null) {
            throw new OperationException("找不到用户：[%s]！", userName);
        }
        PackageCardModel cardModel = packageCardMapper.selectByNameDistinct(cardName);
        if (cardModel == null) {
            throw new OperationException("找不到卡片：[%s]！", cardName);
        }
        UserCardListModel cardListModel = userCardListMapper.selectByUserCard(targetUser.getUserId(), cardModel.getCardPk());
        if (cardListModel == null) {
            return 0;
        }

        return cardListModel.getCount();
    }

    @Override
    public int getUserDailyReward(String name) {
        UserDataModel userDataModel = userDataMapper.selectByNameDistinct(name);
        if (userDataModel == null) {
            return 0;
        }
        return userDataModel.getDailyAward();
    }
}
