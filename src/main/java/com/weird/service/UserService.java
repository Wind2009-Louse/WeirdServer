package com.weird.service;

import com.weird.model.dto.CardSwapDTO;
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
     * 根据用户名查找用户
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
     * @param name 用户名
     * @return 是否添加成功
     */
    boolean addUser(String name) throws Exception;

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
     * 修改用户尘数
     *
     * @param name     用户名
     * @param newCount 新尘数
     * @return 是否更改成功
     */
    boolean updateDust(String name, int newCount) throws Exception;

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
     * 修改不出货数量
     *
     * @param name     用户名
     * @param newCount 新结果
     * @return 是否更改成功
     */
    boolean updateAward(String name, int newCount) throws Exception;

    /**
     * 修改DP
     *
     * @param name     用户名
     * @param newCount 新DP
     * @return 是否更改成功
     */
    boolean updateDuelPoint(String name, int newCount) throws Exception;

    /**
     * 交换两个用户持有的卡片
     *
     * @param dto 参数
     * @return 是否交换成功
     */
    String swapCard(CardSwapDTO dto) throws Exception;
}
