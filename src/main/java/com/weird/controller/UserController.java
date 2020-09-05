package com.weird.controller;

import com.weird.utils.PageResult;
import com.weird.model.dto.UserDataDTO;
import com.weird.model.enums.LoginTypeEnum;
import com.weird.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    /**
     * 【ALL】查询用户信息
     *
     * @param page 页号
     * @param userName 用户名
     * @return 查询结果
     */
    @RequestMapping("/user/list")
    PageResult<UserDataDTO> getDustList(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "user", required = false, defaultValue = "") String userName) throws Exception {
        List<UserDataDTO> dtoList = userService.getListByName(userName);
        PageResult<UserDataDTO> result = new PageResult<>();
        result.addPageInfo(dtoList, page);
        return result;
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
    @RequestMapping("/user/dust")
    String updateDust(
            @RequestParam(value = "target") String targetUser,
            @RequestParam(value = "count") int dustCount,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN){
            throw new Exception("权限不足！");
        }

        if (userService.updateDust(targetUser, dustCount)){
            return "修改成功！";
        } else {
            throw new Exception("修改失败！");
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
    @RequestMapping("/user/change")
    String dustToCard(@RequestParam(value = "card") String cardName,
                       @RequestParam(value = "name") String name,
                       @RequestParam(value = "password") String password) throws Exception {
        // 玩家权限验证
        if (userService.checkLogin(name, password) == LoginTypeEnum.UNLOGIN){
            throw new Exception("登录信息错误！");
        }

        // TODO
        return "";
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
    @RequestMapping("/user/award")
    String updateCount(@RequestParam(value = "target") String targetUser,
                        @RequestParam(value = "award") int awardCount,
                        @RequestParam(value = "name") String name,
                        @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN){
            throw new Exception("权限不足！");
        }

        if (userService.updateAward(targetUser, awardCount)){
            return "修改成功！";
        } else {
            throw new Exception("修改失败！");
        }
    }

    /**
     * 【管理端】添加新用户
     * 新用户密码默认为123456
     *
     * @param target 用户名
     * @param name       操作用户名称
     * @param password   操作用户密码
     * @return 是否修改成功
     */
    @RequestMapping("/user/add")
    boolean addUser(@RequestParam(value = "target") String target,
                    @RequestParam(value = "name") String name,
                    @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN){
            throw new Exception("权限不足！");
        }
        if (name == null || name.length() == 0){
            throw new Exception("用户名为空！");
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
    String updatePassword(@RequestParam(value = "name") String name,
                    @RequestParam(value = "old") String oldPassword,
                    @RequestParam(value = "new") String newPassword) throws Exception{
        if (userService.updatePassword(name, oldPassword, newPassword)){
            return "修改成功！";
        } else {
            return "修改失败！";
        }
    }
}
