package com.weird.controller;

import com.weird.aspect.TrimArgs;
import com.weird.model.dto.RouletteConfigDTO;
import com.weird.model.dto.RouletteResultDTO;
import com.weird.model.enums.LoginTypeEnum;
import com.weird.model.param.RouletteConfigParam;
import com.weird.model.param.UserCheckParam;
import com.weird.service.RouletteService;
import com.weird.service.UserService;
import com.weird.utils.OperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 转盘相关
 *
 * @author Nidhogg
 * @date 2021.9.22
 */
@RestController
@TrimArgs
public class RouletteController {
    @Autowired
    UserService userService;

    @Autowired
    RouletteService rouletteService;

    /**
     * 【ALL】获取转盘配置列表
     *
     * @return 转盘配置
     */
    @PostMapping("/weird_project/roulette/list")
    public List<RouletteConfigDTO> listConfig() {
        return rouletteService.listConfig();
    }

    /**
     * 【管理端】更新转盘配置
     *
     * @param param 配置参数
     * @return 更新结果
     */
    @PostMapping("/weird_project/roulette/update")
    public String updateConfig(@RequestBody RouletteConfigParam param) throws OperationException {
        // 管理权限验证
        if (userService.checkLogin(param.getName(), param.getPassword()) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }

        return rouletteService.updateConfig(param.getList(), param.getName());
    }

    /**
     * 【ALL】转盘
     *
     * @param param 配置参数
     * @return 更新结果
     */
    @PostMapping("/weird_project/roulette/run")
    public RouletteResultDTO roulette(@RequestBody UserCheckParam param) throws OperationException {
        // 管理权限验证
        if (userService.checkLogin(param.getName(), param.getPassword()) == LoginTypeEnum.UNLOGIN) {
            throw new OperationException("你未登录！");
        }

        return rouletteService.roulette(param.getName());
    }
}
