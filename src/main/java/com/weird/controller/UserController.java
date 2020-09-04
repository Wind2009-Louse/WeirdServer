package com.weird.controller;

import com.weird.model.PageResult;
import com.weird.model.dto.UserDataDTO;
import org.springframework.web.bind.annotation.RequestParam;

public class UserController {
    /**
     * 【ALL】查询用户信息
     *
     * @param page 页号
     * @return 查询结果
     */
    PageResult<UserDataDTO> getDustList(
            @RequestParam(value = "page") int page
    ){
        // TODO
        return null;
    }

    /**
     * 【管理端】修改用户的尘数
     *
     * @param userName 用户名
     * @param dustCount 新尘数
     * @return 是否修改成功
     */
    boolean updateDust(String userName, int dustCount){
        // TODO
        return false;
    }

    /**
     * 【玩家端】直接将尘转换为卡片
     *
     * @param userName 用户名
     * @param password 密码
     * @param cardName 卡片名
     * @return 是否转换成功
     */
    boolean dustToCard(String userName, String password, String cardName){
        // TODO
        return false;
    }

    /**
     * 【管理端】修改用户不出货数量
     *
     * @param userName 用户名
     * @param awardCount 不出货数量
     * @return 是否修改成功
     */
    boolean updateCount(String userName, int awardCount){
        // TODO
        return false;
    }
}
