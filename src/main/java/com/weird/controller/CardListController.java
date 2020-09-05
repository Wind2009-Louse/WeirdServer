package com.weird.controller;

import com.weird.utils.PageResult;
import com.weird.model.dto.PackageCardDTO;
import com.weird.model.enums.LoginTypeEnum;
import com.weird.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CardListController {
    @Autowired
    UserService userService;

    /**
     * 【管理端/ALL】卡片搜索
     *
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param targetUser  用户名
     * @param name        操作用户名称
     * @param password    操作用户密码
     * @return 搜索结果
     */
    PageResult<PackageCardDTO> searchCardListAdmin(
            @RequestParam(value = "package", required = false) String packageName,
            @RequestParam(value = "target", required = false) String targetUser,
            @RequestParam(value = "card", required = false) String cardName,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "password") String password) {
        // 管理权限验证
        if (userService.checkLogin(name, password) == LoginTypeEnum.ADMIN) {
            // TODO
            // 搜索全卡
            return null;
        } else {
            // TODO
            // 搜索现存卡
            return null;
        }
    }

    /**
     * 【管理端】修改用户持有的卡片数量
     *
     * @param targetUser 用户名
     * @param cardName   卡片名
     * @param newCount   新持有量
     * @param name       操作用户名称
     * @param password   操作用户密码
     * @return 是否修改成功
     */
    boolean updateUserCardCount(
            @RequestParam(value = "target") String targetUser,
            @RequestParam(value = "card") String cardName,
            @RequestParam(value = "count") int newCount,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new Exception("权限不足！");
        }

        // TODO
        return false;
    }

    /**
     * 【管理端】修改卡包中的卡片名称
     *
     * @param packageName 卡包名
     * @param oldCardName 旧卡片名
     * @param newCardName 新卡片名
     * @param name        操作用户名称
     * @param password    操作用户密码
     * @return 是否修改成功
     */
    boolean updateCardName(
            @RequestParam(value = "package", required = false) String packageName,
            @RequestParam(value = "oldname") String oldCardName,
            @RequestParam(value = "newname") String newCardName,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new Exception("权限不足！");
        }

        // TODO
        return false;
    }

    /**
     * 【管理端】添加卡片信息
     *
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param rare        稀有度
     * @param name        操作用户名称
     * @param password    操作用户密码
     * @return 是否添加成功
     */
    boolean addCardDetail(
            @RequestParam(value = "package") String packageName,
            @RequestParam(value = "cardname") String cardName,
            @RequestParam(value = "rare") String rare,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new Exception("权限不足！");
        }

        // TODO
        return false;
    }

    /**
     * 【管理端】删除卡片信息
     *
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param name        操作用户名称
     * @param password    操作用户密码
     * @return 是否删除成功
     */
    boolean deleteCardDetail(@RequestParam(value = "package") String packageName,
                             @RequestParam(value = "cardname") String cardName,
                             @RequestParam(value = "name") String name,
                             @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new Exception("权限不足！");
        }

        // TODO
        return false;
    }
}
