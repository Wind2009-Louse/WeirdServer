package com.weird.controller;

import com.weird.model.enums.LoginTypeEnum;
import com.weird.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;

public class PackageController {
    @Autowired
    UserService userService;

    /**
     * 【管理端】新增卡包
     *
     * @param packageName 卡包名
     * @param name        操作用户名称
     * @param password    操作用户密码
     * @return 是否修改成功
     */
    boolean addPackage(@RequestParam(value = "package") String packageName,
                       @RequestParam(value = "name") String name,
                       @RequestParam(value = "password") String password) {
        // 管理权限验证
        if (userService.checkLogin(name, password) == LoginTypeEnum.ADMIN) {
            return false;
        }

        // TODO
        return false;
    }

    /**
     * 【管理端】修改卡包名
     *
     * @param oldPackageName 旧卡包名
     * @param newPackageName 新卡包名
     * @param name           操作用户名称
     * @param password       操作用户密码
     * @return 是否修改成功
     */
    boolean updatePackageName(@RequestParam(value = "oldname") String oldPackageName,
                              @RequestParam(value = "newname") String newPackageName,
                              @RequestParam(value = "name") String name,
                              @RequestParam(value = "password") String password) {
        // 管理权限验证
        if (userService.checkLogin(name, password) == LoginTypeEnum.ADMIN) {
            return false;
        }

        // TODO
        return false;
    }
}
