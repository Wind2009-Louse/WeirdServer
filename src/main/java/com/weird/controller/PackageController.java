package com.weird.controller;

import com.weird.model.enums.LoginTypeEnum;
import com.weird.service.PackageService;
import com.weird.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PackageController {
    @Autowired
    UserService userService;

    @Autowired
    PackageService packageService;

    /**
     * 【管理端】新增卡包
     *
     * @param packageName 卡包名
     * @param name        操作用户名称
     * @param password    操作用户密码
     * @return 是否修改成功
     */
    @RequestMapping("/package/add")
    String addPackage(@RequestParam(value = "package") String packageName,
                       @RequestParam(value = "name") String name,
                       @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new Exception("权限不足！");
        }
        if (packageName == null || packageName.length() == 0){
            throw new Exception("卡包名为空！");
        }

        if (packageService.addPackage(packageName)){
            return "新增成功！";
        } else {
            throw new Exception("新增失败！");
        }
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
    @RequestMapping("/package/update")
    String updatePackageName(@RequestParam(value = "oldname") String oldPackageName,
                              @RequestParam(value = "newname") String newPackageName,
                              @RequestParam(value = "name") String name,
                              @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new Exception("权限不足！");
        }
        if (newPackageName == null || newPackageName.length() == 0){
            throw new Exception("卡包名为空！");
        }

        if (packageService.updatePackageName(oldPackageName, newPackageName)){
            return "修改成功！";
        } else {
            throw new Exception("修改失败！");
        }
    }
}
