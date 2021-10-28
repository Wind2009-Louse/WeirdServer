package com.weird.service;

import com.weird.model.dto.CardSwapDTO;
import com.weird.model.dto.UserDataDTO;
import com.weird.model.enums.LoginTypeEnum;
import com.weird.model.param.ReplaceCardParam;
import com.weird.utils.OperationException;

import java.util.List;

/**
 * 用户Service
 *
 * @author Nidhogg
 * @date 2020.9.4
 */
public interface UserService {
    /**
     * 根据用户名查找用户列表
     *
     * @param name 用户名
     * @return 用户列表
     */
    List<UserDataDTO> getListByName(String name);

    /**
     * 根据用户名查找用户
     *
     * @param name 用户名
     * @return 用户
     */
    UserDataDTO getUserByName(String name);

    /**
     * 根据用户ID查找用户
     *
     * @param id 用户ID
     * @return 用户
     */
    UserDataDTO getUserById(int id);

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
     * @param name     用户名
     * @param operator 操作人
     * @return 是否添加成功
     */
    boolean addUser(String name, String operator) throws Exception;

    /**
     * 修改用户密码
     *
     * @param name        用户名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否更改成功
     */
    boolean updatePassword(String name, String oldPassword, String newPassword) throws Exception;

    /**
     * 重置用户密码
     *
     * @param name     用户名
     * @param operator 操作人
     * @return 是否更改成功
     */
    boolean resetPassword(String name, String operator) throws Exception;

    /**
     * 修改用户尘数
     *
     * @param name     用户名
     * @param newCount 新尘数
     * @param operator 操作人
     * @return 是否更改成功
     */
    String updateDust(String name, int newCount, String operator) throws Exception;

    /**
     * 将尘转换成卡片
     *
     * @param cardName 卡片名称
     * @param userName 用户名
     * @param password 密码
     * @return 是否转换成功
     */
    boolean dustToCard(String cardName, String userName, String password) throws Exception;

    /**
     * 将尘转换成指定卡包的随机闪
     *
     * @param packageName 卡包名
     * @param userName    用户名
     * @param password    密码
     * @param dustFirst   优先使用尘
     * @return 转换结果
     */
    String dustToRare(String packageName, String userName, String password, int dustFirst) throws Exception;

    /**
     * 用硬币更换卡片
     *
     * @param cardName    卡片名
     * @param userName    用户名
     * @param password    密码
     * @return 转换结果
     */
    String coinToCard(String cardName, String userName, String password) throws Exception;


    /**
     * 将多余的闪卡换成尘
     *
     * @param userName 用户名
     * @param password 密码
     * @param cardName 卡片名称
     * @param count    转换的数量
     * @return 是否转换成功
     */
    int rareToDust(String userName, String password, String cardName, int count) throws Exception;

    /**
     * 修改不出货数量
     *
     * @param name     用户名
     * @param newCount 新结果
     * @param operator 操作人
     * @return 是否更改成功
     */
    String updateAward(String name, int newCount, String operator) throws Exception;

    /**
     * 修改DP
     *
     * @param name     用户名
     * @param newCount 新DP
     * @return 是否更改成功
     */
    String updateDuelPoint(String name, int newCount, String operator) throws Exception;

    /**
     * 修改硬币数量
     *
     * @param name     用户名
     * @param newCount 新硬币数量
     * @param operator 操作人
     */
    String updateCoin(String name, int newCount, String operator) throws Exception;

    /**
     * 修改转盘次数
     *
     * @param name     用户名
     * @param newCount 新硬币数量
     * @param operator 操作人
     */
    String updateRoulette(String name, int newCount, String operator) throws Exception;

    /**
     * 修改转盘次数
     *
     * @param name     用户名
     * @param newCount 新硬币数量
     * @param operator 操作人
     */
    String updateRollCount(String name, int newCount, String operator) throws Exception;

    /**
     * 交换两个用户持有的卡片
     *
     * @param dto 参数
     * @return 是否交换成功
     */
    String swapCard(CardSwapDTO dto, String operator) throws OperationException;

    /**
     * 替换玩家持有的卡片
     *
     * @param param 参数
     * @return
     * @throws Exception
     */
    String exchangeOwnCard(ReplaceCardParam param) throws Exception;

    /**
     * 根据QQ号查找用户
     *
     * @param qq QQ号
     * @return 用户
     */
    UserDataDTO getUserByQQ(String qq);

    /**
     * 根据用户名查找用户
     *
     * @param name 用户名
     * @return 用户
     */
    boolean updateQQ(String name, String qq) throws OperationException;

    /**
     * 解除对应QQ号的绑定关系
     *
     * @param qq QQ号
     * @return 是否解除失败
     */
    boolean unbindQQ(String qq);

    /**
     * 检查该用户是否为管理员
     *
     * @param userName 用户名
     * @return 是否为管理员
     */
    boolean adminCheck(String userName);

    /**
     * 设置用户禁用状态
     *
     * @param target   用户
     * @param operator 操作者
     * @param disabled 是否禁用
     * @return 是否成功
     */
    boolean updateDisabled(String target, String operator, int disabled);

    /**
     * 查看禁用中的用户
     *
     * @return
     */
    List<String> showDisabledUserName();

    /**
     * 获取用户的百八数量
     *
     * @param userName 用户
     * @return
     */
    int getDoubleRareCount(String userName);

    /**
     * 更新用户的百八数量
     *
     * @param userName
     * @param count
     * @param operator
     * @return
     */
    boolean updateDoubleRareCount(String userName, int count, String operator);

    /**
     * 重置所有用户的百八数量
     *
     * @param count
     * @return
     */
    boolean resetDoubleRareCount(int count);
}
