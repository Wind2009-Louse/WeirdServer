package com.weird.controller;

import com.weird.model.dto.UserDataDTO;
import com.weird.model.enums.LoginTypeEnum;
import com.weird.service.CardService;
import com.weird.service.UserService;
import com.weird.utils.OperationException;
import com.weird.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户信息相关
 *
 * @author Nidhogg
 */
@RestController
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    CardService cardService;

    /**
     * 【ALL】查询用户信息
     *
     * @param page     页号
     * @param userName 用户名
     * @return 查询结果
     */
    @RequestMapping("/weird_project/user/list")
    public PageResult<UserDataDTO> getDustList(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "pagesize", required = false, defaultValue = "20") int pageSize,
            @RequestParam(value = "user", required = false, defaultValue = "") String userName) throws Exception {
        List<UserDataDTO> dtoList = userService.getListByName(userName);
        PageResult<UserDataDTO> result = new PageResult<>();
        result.addPageInfo(dtoList, page, pageSize);
        return result;
    }

    /**
     * 【ALL】检查用户类型
     *
     * @param name     用户名
     * @param password 用户密码
     * @return UNLOGIN(未登录)、ADMIN(管理员)、(NORMAL)普通用户
     */
    @RequestMapping("/weird_project/user/check")
    public LoginTypeEnum getLoginType(@RequestParam(value = "name") String name,
                                      @RequestParam(value = "password") String password) {
        return userService.checkLogin(name, password);
    }


    /**
     * 【管理端】修改用户持有的卡片数量
     *
     * @param targetUser  用户名
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param newCount    新持有量
     * @param name        操作用户名称
     * @param password    操作用户密码
     * @return 是否修改成功
     */
    @RequestMapping("/weird_project/user/card/update")
    public String updateUserCardCount(
            @RequestParam(value = "target") String targetUser,
            @RequestParam(value = "card") String cardName,
            @RequestParam(value = "count") int newCount,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }

        if (cardService.updateCardCount(targetUser, cardName, newCount)) {
            return "修改成功！";
        } else {
            throw new OperationException("修改失败！");
        }
    }

    /**
     * 【管理端】修改用户的尘数
     *
     * @param targetUser 用户名
     * @param dustCount  新尘数
     * @param name       操作用户名称
     * @param password   操作用户密码
     * @return 是否修改成功
     */
    @RequestMapping("/weird_project/user/dust")
    public String updateDust(
            @RequestParam(value = "target") String targetUser,
            @RequestParam(value = "count") int dustCount,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }
        if (targetUser == null || targetUser.length() == 0) {
            throw new OperationException("用户名为空！");
        }

        if (userService.updateDust(targetUser, dustCount)) {
            return "修改成功！";
        } else {
            throw new OperationException("修改失败！");
        }
    }

    /**
     * 【玩家端】直接将尘转换为卡片
     *
     * @param cardName 卡片名
     * @param name     操作用户名称
     * @param password 操作用户密码
     * @return 是否转换成功
     */
    @RequestMapping("/weird_project/user/card/change")
    public String dustToCard(@RequestParam(value = "card") String cardName,
                             @RequestParam(value = "name") String name,
                             @RequestParam(value = "password") String password) throws Exception {
        if (userService.dustToCard(cardName, name, password)) {
            return "转换成功！";
        } else {
            throw new OperationException("转换失败！");
        }
    }

    /**
     * 【管理端】修改用户不出货数量
     *
     * @param targetUser 用户名
     * @param awardCount 不出货数量
     * @param name       操作用户名称
     * @param password   操作用户密码
     * @return 是否修改成功
     */
    @RequestMapping("/weird_project/user/award")
    public String updateCount(@RequestParam(value = "target") String targetUser,
                              @RequestParam(value = "award") int awardCount,
                              @RequestParam(value = "name") String name,
                              @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }
        if (targetUser == null || targetUser.length() == 0) {
            throw new OperationException("用户名为空！");
        }

        if (userService.updateAward(targetUser, awardCount)) {
            return "修改成功！";
        } else {
            throw new OperationException("修改失败！");
        }
    }

    /**
     * 【管理端】修改用户DP
     *
     * @param targetUser 用户名
     * @param dpCount    新DP
     * @param name       操作用户名称
     * @param password   操作用户密码
     * @return 是否修改成功
     */
    @RequestMapping("/weird_project/user/dp")
    public String updateDuelPoint(
            @RequestParam(value = "target") String targetUser,
            @RequestParam(value = "dp") int dpCount,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }
        if (targetUser == null || targetUser.length() == 0) {
            throw new OperationException("用户名为空！");
        }

        if (userService.updateDuelPoint(targetUser, dpCount)) {
            return "修改成功！";
        } else {
            throw new OperationException("修改失败！");
        }
    }

    /**
     * 【管理端】添加新用户
     * 新用户密码默认为123456
     *
     * @param target   用户名
     * @param name     操作用户名称
     * @param password 操作用户密码
     * @return 是否添加成功
     */
    @RequestMapping("/weird_project/user/add")
    public String addUser(@RequestParam(value = "target") String target,
                          @RequestParam(value = "name") String name,
                          @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }
        if (target == null || target.length() == 0) {
            throw new OperationException("用户名为空！");
        }
        if (userService.addUser(target)) {
            return "添加成功！";
        } else {
            throw new OperationException("添加失败！");
        }
    }

    /**
     * 【ALL】修改用户密码
     *
     * @param name        用户名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    @RequestMapping("/weird_project/user/pw")
    public String updatePassword(@RequestParam(value = "name") String name,
                                 @RequestParam(value = "old") String oldPassword,
                                 @RequestParam(value = "new") String newPassword) throws Exception {
        if (userService.updatePassword(name, oldPassword, newPassword)) {
            return "修改成功！";
        } else {
            return "修改失败！";
        }
    }
}
