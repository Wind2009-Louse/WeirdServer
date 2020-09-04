package com.weird.controller;

import com.weird.model.PageResult;
import com.weird.model.dto.PackageCardDTO;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CardListController {
    /**
     * 【管理端】卡片搜索
     *
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param userName    用户名
     * @return 搜索结果
     */
    PageResult<PackageCardDTO> searchCardListAdmin(
            @RequestParam(value = "package", required = false) String packageName,
            @RequestParam(value = "user", required = false) String userName,
            @RequestParam(value = "card", required = false) String cardName) {
        // TODO
        return null;
    }

    /**
     * 【玩家端】卡片搜索
     *
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param userName    用户名
     * @return 搜索结果
     */
    PageResult<PackageCardDTO> searchCardList(
            @RequestParam(value = "package", required = false) String packageName,
            @RequestParam(value = "user", required = false) String userName,
            @RequestParam(value = "card", required = false) String cardName) {
        // TODO
        return null;
    }

    /**
     * 【管理端】修改用户持有的卡片数量
     *
     * @param userName 用户名
     * @param cardName 卡片名
     * @param newCount 新持有量
     * @return 是否修改成功
     */
    boolean updateUserCardCount(
            @RequestParam(value = "user") String userName,
            @RequestParam(value = "card") String cardName,
            @RequestParam(value = "count") int newCount
    ) {
        return false;
    }

    /**
     * 【管理端】修改卡包中的卡片名称
     *
     * @param packageName 卡包名
     * @param oldCardName 旧卡片名
     * @param newCardName 新卡片名
     * @return 是否修改成功
     */
    boolean updateCardName(
            @RequestParam(value = "package", required = false) String packageName,
            @RequestParam(value = "oldname") String oldCardName,
            @RequestParam(value = "newname") String newCardName
    ) {
        // TODO
        return false;
    }

    /**
     * 【管理端】添加卡片信息
     *
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param rare        稀有度
     * @return 是否添加成功
     */
    boolean addCardInfo(
            @RequestParam(value = "package") String packageName,
            @RequestParam(value = "cardname") String cardName,
            @RequestParam(value = "rare") String rare
    ) {
        // TODO
        return false;
    }

    /**
     * 【管理端】删除卡片信息
     *
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @return 是否删除成功
     */
    boolean deleteCardInfo(@RequestParam(value = "package") String packageName,
                           @RequestParam(value = "cardname") String cardName) {
        // TODO
        return false;
    }
}
