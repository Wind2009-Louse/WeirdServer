package com.weird.controller;

import com.weird.model.PageResult;
import com.weird.model.dto.UserDataDTO;
import com.weird.model.enums.LoginTypeEnum;
import com.weird.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    /**
     * 【ALL】查询用户信息
     *
     * @param page 页号
     * @return 查询结果
     */
    PageResult<UserDataDTO> getDustList(
            @RequestParam(value = "page") int page
    ) {
        // TODO
        return null;
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
    boolean updateDust(
            @RequestParam(value = "target") String targetUser,
            @RequestParam(value = "count") int dustCount,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "password") String password) {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN){
            return false;
        }

        // TODO
        return false;
    }

    /**
     * 【玩家端】直接将尘转换为卡片
     *
     * @param cardName 卡片名
     * @param name     操作用户名称
     * @param password 操作用户密码
     * @return 是否转换成功
     */
    boolean dustToCard(@RequestParam(value = "card") String cardName,
                       @RequestParam(value = "name") String name,
                       @RequestParam(value = "password") String password) {
        // 玩家权限验证
        if (userService.checkLogin(name, password) == LoginTypeEnum.UNLOGIN){
            return false;
        }

        // TODO
        return false;
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
    boolean updateCount(@RequestParam(value = "target") String targetUser,
                        @RequestParam(value = "award") int awardCount,
                        @RequestParam(value = "name") String name,
                        @RequestParam(value = "password") String password) {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN){
            return false;
        }

        // TODO
        return false;
    }

    /**
     * 【管理端】添加新用户
     *
     * @param target 用户名
     * @param name       操作用户名称
     * @param password   操作用户密码
     * @return 是否修改成功
     */
    @RequestMapping("/user/add")
    boolean addUser(@RequestParam(value = "target") String target,
                    @RequestParam(value = "name") String name,
                    @RequestParam(value = "password") String password){
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN){
            return false;
        }
        return userService.addUser(target);
    }

    /**
     * 【ALL】修改用户密码
     *
     * @param name 用户名
     * @param oldPassword   旧密码
     * @param newPassword   新密码
     * @return 是否修改成功
     */
    @RequestMapping("/user/pw")
    boolean updatePassword(@RequestParam(value = "name") String name,
                    @RequestParam(value = "old") String oldPassword,
                    @RequestParam(value = "new") String newPassword){
        return userService.updatePassword(name, oldPassword, newPassword);
    }
}
