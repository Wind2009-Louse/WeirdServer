package com.weird.controller;

import com.weird.model.PageResult;
import com.weird.model.RollListModel;
import com.weird.model.dto.RollListDTO;
import com.weird.service.RollService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RollController {
    @Autowired
    RollService rollService;

    /**
     * 【管理端】发送抽卡信息（诡异UI用）
     *
     * @param targetUser  用户名
     * @param packageName 卡包名
     * @param cardId1     卡片ID1
     * @param cardId2     卡片ID2
     * @param cardId3     卡片ID3
     * @param name        操作用户名称
     * @param password    操作用户密码
     * @return 抽卡是否成功
     */
    @RequestMapping("weirdUI/roll")
    String roll(@RequestParam(value = "target") String targetUser,
                @RequestParam(value = "package") String packageName,
                @RequestParam(value = "card1") long cardId1,
                @RequestParam(value = "card2") long cardId2,
                @RequestParam(value = "card3") long cardId3,
                @RequestParam(value = "name") String name,
                @RequestParam(value = "password") String password) {
        // TODO
        String wrongMessage = "";

        // 错误处理

        if (wrongMessage.length() > 0) {
            return wrongMessage;
        }

        return "记录成功!";
    }

    /**
     * 【管理端/ALL?】查询抽卡结果
     *
     * @param page     当前页码
     * @param userName 抽卡用户名
     * @return 抽卡结果
     */
    PageResult<RollListDTO> getRollList(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "user", required = false) String userName) {
        // TODO
        return null;
    }

    /**
     * 【管理端】设置某个抽卡结果是否适用
     *
     * @param rollId   抽卡结果ID
     * @param status   要设置的状态
     * @param name     操作用户名称
     * @param password 操作用户密码
     * @return 是否成功
     */
    boolean setRollStatus(@RequestParam(value = "id") long rollId,
                          @RequestParam(value = "status") int status,
                          @RequestParam(value = "name") String name,
                          @RequestParam(value = "password") String password) {
        // TODO
        return false;
    }
}
