package com.weird.controller;

import com.weird.model.PageResult;
import com.weird.model.dto.PackageCardDTO;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CardListController {
    /**
     * 管理端使用的卡片搜索
     * @param packageName 卡包名
     * @param cardName 卡片名
     * @param userName 用户名
     * @return 搜索结果
     */
    PageResult<PackageCardDTO> searchCardListAdmin(
            @RequestParam(value = "package", required = false) String packageName,
            @RequestParam(value = "user", required = false) String userName,
            @RequestParam(value = "card", required = false) String cardName){
        // TODO
        return null;
    }

    /**
     * 普通用户使用的卡片搜索
     * @param packageName 卡包名
     * @param cardName 卡片名
     * @param userName 用户名
     * @return 搜索结果
     */
    PageResult<PackageCardDTO> searchCardList(
            @RequestParam(value = "package", required = false) String packageName,
            @RequestParam(value = "user", required = false) String userName,
            @RequestParam(value = "card", required = false) String cardName){
        // TODO
        return null;
    }
}
