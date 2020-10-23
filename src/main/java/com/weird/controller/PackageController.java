package com.weird.controller;

import com.weird.aspect.TrimArgs;
import com.weird.model.PackageInfoModel;
import com.weird.model.param.PackageSortParam;
import com.weird.model.enums.LoginTypeEnum;
import com.weird.service.PackageService;
import com.weird.service.UserService;
import com.weird.utils.OperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 卡包相关
 *
 * @author Nidhogg
 */
@RestController
@TrimArgs
public class PackageController {
    @Autowired
    UserService userService;

    @Autowired
    PackageService packageService;

    /**
     * 【ALL】查询卡包列表
     *
     * @param packageName 卡包名（模糊搜索）
     * @return 结果列表
     */
    @RequestMapping("/weird_project/package/list")
    public List<PackageInfoModel> getPackages(
            @RequestParam(value = "name", required = false, defaultValue = "") String packageName
    ) throws Exception {
        return packageService.selectByName(packageName);
    }

    /**
     * 【管理端】新增卡包
     *
     * @param packageName 卡包名
     * @param name        操作用户名称
     * @param password    操作用户密码
     * @return 是否修改成功
     */
    @RequestMapping("/weird_project/package/add")
    public String addPackage(@RequestParam(value = "package") String packageName,
                             @RequestParam(value = "name") String name,
                             @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }
        if (packageName == null || packageName.length() == 0) {
            throw new OperationException("卡包名为空！");
        }

        if (packageService.addPackage(packageName)) {
            return "新增成功！";
        } else {
            throw new OperationException("新增失败！");
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
    @RequestMapping("/weird_project/package/update")
    public String updatePackageName(@RequestParam(value = "oldname") String oldPackageName,
                                    @RequestParam(value = "newname") String newPackageName,
                                    @RequestParam(value = "name") String name,
                                    @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }
        if (newPackageName == null || newPackageName.length() == 0) {
            throw new OperationException("卡包名为空！");
        }
        if (oldPackageName.equals(newPackageName)) {
            throw new OperationException("名字未修改！");
        }

        if (packageService.updatePackageName(oldPackageName, newPackageName)) {
            return "修改成功！";
        } else {
            throw new OperationException("修改失败！");
        }
    }


    @PostMapping("/weird_project/package/sort")
    public String sortPackage(@RequestBody PackageSortParam param) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(param.getName(), param.getPassword()) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }

        return packageService.sort(param.getPackageIndexList());
    }
}
