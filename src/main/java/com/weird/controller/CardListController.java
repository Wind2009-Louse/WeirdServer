package com.weird.controller;

import com.weird.model.dto.CardHistoryDTO;
import com.weird.model.dto.CardListDTO;
import com.weird.model.dto.CardOwnListDTO;
import com.weird.model.enums.LoginTypeEnum;
import com.weird.service.CardService;
import com.weird.service.PackageService;
import com.weird.service.UserService;
import com.weird.utils.OperationException;
import com.weird.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * 卡片列表相关
 *
 * @author Nidhogg
 */
@RestController
public class CardListController {
    @Autowired
    UserService userService;

    @Autowired
    PackageService packageService;

    @Autowired
    CardService cardService;

    final List<String> RARE_LIST = Arrays.asList("N", "R", "SR", "UR", "HR");

    /**
     * 【管理端】全卡片搜索
     *
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param rare        稀有度
     * @param page        页码
     * @param pageSize    页面大小
     * @param name        操作用户名称
     * @param password    操作用户密码
     * @return 搜索结果
     */
    @RequestMapping("/weird_project/card/list/admin")
    public PageResult<CardListDTO> searchCardList(
            @RequestParam(value = "package", required = false, defaultValue = "") String packageName,
            @RequestParam(value = "card", required = false, defaultValue = "") String cardName,
            @RequestParam(value = "rare", required = false, defaultValue = "") String rare,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "pagesize", required = false, defaultValue = "20") int pageSize,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }
        List<CardListDTO> dtoList = cardService.selectListAdmin(packageName, cardName, rare);
        PageResult<CardListDTO> result = new PageResult<>();
        result.addPageInfo(dtoList, page, pageSize);
        return result;
    }

    /**
     * 【ALL】全卡片拥有情况搜索
     *
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param rare        稀有度
     * @param targetUser  用户名
     * @param page        页码
     * @param pageSize    页面大小
     * @return 搜索结果
     */
    @RequestMapping("/weird_project/card/list")
    public PageResult<CardOwnListDTO> searchCardOwnList(
            @RequestParam(value = "package", required = false, defaultValue = "") String packageName,
            @RequestParam(value = "card", required = false, defaultValue = "") String cardName,
            @RequestParam(value = "rare", required = false, defaultValue = "") String rare,
            @RequestParam(value = "target", required = false, defaultValue = "") String targetUser,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "pagesize", required = false, defaultValue = "20") int pageSize) throws Exception {
        List<CardOwnListDTO> dtoList = cardService.selectList(packageName, cardName, rare, targetUser);
        PageResult<CardOwnListDTO> result = new PageResult<>();
        result.addPageInfo(dtoList, page, pageSize);
        return result;
    }

    /**
     * 查询卡片更改的历史纪录
     *
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param page        页码
     * @param pageSize    页面大小
     * @return 查询结果
     */
    @RequestMapping("/weird_project/card/history")
    public List<CardHistoryDTO> searchHistory(
            @RequestParam(value = "package", required = false, defaultValue = "") String packageName,
            @RequestParam(value = "card", required = false, defaultValue = "") String cardName,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "pagesize", required = false, defaultValue = "20") int pageSize
    ){
        return null;
    }

    /**
     * 【管理端】添加卡片信息
     *
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param rare        稀有度
     * @param name        操作用户名称
     * @param password    操作用户密码
     * @return 是否添加成功
     */
    @RequestMapping("/weird_project/card/add")
    public String addCardDetail(
            @RequestParam(value = "package") String packageName,
            @RequestParam(value = "card") String cardName,
            @RequestParam(value = "rare") String rare,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }
        if (cardName == null || cardName.length() == 0) {
            throw new OperationException("卡片名为空！");
        }

        // 稀有度验证
        if (!RARE_LIST.contains(rare)) {
            throw new OperationException("稀有度设置错误！");
        }

        if (packageService.addCard(packageName, cardName, rare)) {
            return "添加成功！";
        } else {
            throw new OperationException("添加失败！");
        }
    }

    /**
     * 【管理端】修改卡包中的卡片名称
     *
     * @param packageName 卡包名
     * @param oldCardName 旧卡片名
     * @param newCardName 新卡片名
     * @param isShow      是否在历史记录中显示该卡片
     * @param name        操作用户名称
     * @param password    操作用户密码
     * @return 是否修改成功
     */
    @RequestMapping("/weird_project/card/update")
    public String updateCardName(
            @RequestParam(value = "package", required = false) String packageName,
            @RequestParam(value = "oldname") String oldCardName,
            @RequestParam(value = "newname") String newCardName,
            @RequestParam(value= "show", required = false, defaultValue = "0") int isShow,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }
        if (newCardName == null || newCardName.length() == 0) {
            throw new OperationException("卡片名为空！");
        }
        if (newCardName.equals(oldCardName)) {
            throw new OperationException("名字未修改！");
        }

        if (packageService.updateCardName(packageName, oldCardName, newCardName, isShow)) {
            return "修改成功！";
        } else {
            throw new OperationException("修改失败！");
        }
    }

    /**
     * 【管理端】删除卡片信息
     * 需求较低，暂时鸽置
     *
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param name        操作用户名称
     * @param password    操作用户密码
     * @return 是否删除成功
     */
    @RequestMapping("/weird_project/card/delete")
    @Deprecated
    public String deleteCardDetail(@RequestParam(value = "package") String packageName,
                                   @RequestParam(value = "card") String cardName,
                                   @RequestParam(value = "name") String name,
                                   @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }

        throw new OperationException("删除失败！");
    }
}
