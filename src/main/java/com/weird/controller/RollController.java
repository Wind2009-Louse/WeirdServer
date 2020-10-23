package com.weird.controller;

import com.weird.aspect.SearchParamFix;
import com.weird.aspect.TrimArgs;
import com.weird.model.dto.RollListDTO;
import com.weird.model.enums.LoginTypeEnum;
import com.weird.model.param.RollParam;
import com.weird.model.param.SearchRollParam;
import com.weird.service.RollService;
import com.weird.service.UserService;
import com.weird.utils.OperationException;
import com.weird.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 抽卡相关
 *
 * @author Nidhogg
 */
@RestController
@TrimArgs
public class RollController {
    @Autowired
    RollService rollService;

    @Autowired
    UserService userService;

    /**
     * 【RMXP端】发送抽卡信息（诡异UI用）
     *
     * @param targetUser 用户名
     * @param cardName1  卡片名称1
     * @param cardName2  卡片名称2
     * @param cardName3  卡片名称3
     * @param name       操作用户名称
     * @param password   操作用户密码
     * @return 抽卡是否成功
     */
    @RequestMapping("/weird_project/weirdUI/roll")
    public String roll(@RequestParam(value = "target") String targetUser,
                       @RequestParam(value = "card1") String cardName1,
                       @RequestParam(value = "card2") String cardName2,
                       @RequestParam(value = "card3") String cardName3,
                       @RequestParam(value = "name") String name,
                       @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }

        List<String> cardNames = Arrays.asList(cardName1, cardName2, cardName3);
        if (rollService.roll(cardNames, targetUser)) {
            return "记录成功!";
        } else {
            throw new OperationException("抽卡记录失败！");
        }
    }

    /**
     * 【管理端】批量发送抽卡信息
     *
     * @param param 参数
     * @return 抽卡是否成功
     */
    @PostMapping("/weird_project/roll/add")
    public String rollPost(@RequestBody RollParam param) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(param.getName(), param.getPassword()) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }
        if (userService.getUserByName(param.getTarget()) == null) {
            throw new OperationException("找不到该用户：[%s]！", param.getTarget());
        }
        if (param.getCards() == null) {
            throw new OperationException("抽卡列表为空！");
        }

        StringBuilder totalException = new StringBuilder();
        int successCount = 0;
        for (List<String> list : param.getCards()) {
            if (list == null || list.size() == 0) {
                totalException.append("抽卡列表为空！\n");
                continue;
            }
            try {
                if (rollService.roll(list, param.getTarget())) {
                    successCount++;
                } else {
                    totalException.append(String.format("抽卡记录[%s]记录失败！\n", list.toString()));
                }
            } catch (Exception e) {
                totalException.append(e.getMessage());
                totalException.append("\n");
            }

        }

        if (totalException.length() > 0) {
            totalException.append(String.format("共成功记录%d个结果！", successCount));
            return totalException.toString();
        } else {
            return "全部记录成功！";
        }

    }

    /**
     * 【ALL】查询抽卡结果
     *
     * @param param 参数
     * @return 抽卡结果
     */
    @RequestMapping("/weird_project/roll/list")
    @SearchParamFix
    public PageResult<RollListDTO> getRollList(@RequestBody SearchRollParam param) throws Exception {
        return rollService.selectRollList(param);
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
    @RequestMapping("/weird_project/roll/set")
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
