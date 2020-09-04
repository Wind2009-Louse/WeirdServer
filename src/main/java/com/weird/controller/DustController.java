package com.weird.controller;

import com.weird.model.PageResult;
import com.weird.model.UserDustModel;
import org.springframework.web.bind.annotation.RequestParam;

public class DustController {
    /**
     * 查询尘数
     *
     * @param page 页号
     * @return
     */
    PageResult<UserDustModel> getDustList(
            @RequestParam(value = "page") int page
    ){
        // TODO
        return null;
    }

    /**
     * 修改用户的尘数
     *
     * @param userName 用户名
     * @param dustCount 新尘数
     * @return 是否修改成功
     */
    boolean updateDust(String userName, int dustCount){
        // TODO
        return false;
    }
}
