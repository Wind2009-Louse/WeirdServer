package com.weird.controller;

import com.weird.model.RollListModel;
import com.weird.model.dto.RollDetailDTO;
import com.weird.utils.OperationException;
import com.weird.utils.PageResult;
import com.weird.model.dto.RollListDTO;
import com.weird.model.enums.LoginTypeEnum;
import com.weird.service.RollService;
import com.weird.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * 抽卡相关
 *
 * @author Nidhogg
 */
@RestController
public class RollController {
    @Autowired
    RollService rollService;

    @Autowired
    UserService userService;

    /**
     * 【管理端】发送抽卡信息（诡异UI用）
     *
     * @param targetUser  用户名
     * @param packageName 卡包名
     * @param cardName1   卡片名称1
     * @param cardName2   卡片名称2
     * @param cardName3   卡片名称3
     * @param name        操作用户名称
     * @param password    操作用户密码
     * @return 抽卡是否成功
     */
    @RequestMapping("/weirdUI/roll")
    public String roll(@RequestParam(value = "target") String targetUser,
                       @RequestParam(value = "package") String packageName,
                       @RequestParam(value = "card1") String cardName1,
                       @RequestParam(value = "card2") String cardName2,
                       @RequestParam(value = "card3") String cardName3,
                       @RequestParam(value = "name") String name,
                       @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new OperationException("用户信息错误！");
        }

        List<String> cardNames = Arrays.asList(cardName1, cardName2, cardName3);
        if (rollService.roll(packageName, cardNames, targetUser)) {
            return "记录成功!";
        } else {
            throw new OperationException("抽卡记录失败！");
        }
    }

    /**
     * 【ALL】查询抽卡结果
     *
     * @param page     当前页码
     * @param userName 抽卡用户名
     * @return 抽卡结果
     */
    @RequestMapping("/roll/list")
    public PageResult<RollListDTO> getRollList(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "package", required = false, defaultValue = "") String packageName,
            @RequestParam(value = "user", required = false, defaultValue = "") String userName) throws Exception {
        List<RollListDTO> modelList = rollService.selectRollList(packageName, userName);

        // 通过分页截取需要查询详细内容的部分
        PageResult<RollListDTO> resultList = new PageResult<>();
        resultList.addPageInfo(modelList, page);
        List<RollListDTO> cutList = resultList.getDataList();

        resultList.setDataList(rollService.selectRollDetail(cutList));
        return resultList;
    }

    /**
     * 【管理端】设置某个抽卡结果是否适用
     *
     * @param rollId   抽卡结果ID
     * @param status   要设置的状态（0=使用，1=禁用）
     * @param name     操作用户名称
     * @param password 操作用户密码
     * @return 是否成功
     */
    @RequestMapping("/roll/set")
    public String setRollStatus(@RequestParam(value = "id") long rollId,
                                @RequestParam(value = "status") int status,
                                @RequestParam(value = "name") String name,
                                @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }
        if (status != 0 && status != 1) {
            throw new OperationException("状态设置错误！");
        }

        if (rollService.setStatus(rollId, status)) {
            return "修改成功！";
        } else {
            throw new OperationException("修改失败！");
        }
    }
}
