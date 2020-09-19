package com.weird.controller;

import com.weird.model.dto.BatchAddCardParam;
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
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
     * 【ALL】全卡片搜索
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
    @RequestMapping("/weird_project/card/list")
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
            return searchCardListUser(packageName, cardName, rare, page, pageSize);
        } else {
            return searchCardListAdmin(packageName, cardName, rare, page, pageSize, name, password);
        }
    }

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
    public PageResult<CardListDTO> searchCardListAdmin(
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
     * 【玩家端】全已知卡片搜索
     *
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param rare        稀有度
     * @param page        页码
     * @param pageSize    页面大小
     * @return 搜索结果
     */
    @RequestMapping("/weird_project/card/list/user")
    public PageResult<CardListDTO> searchCardListUser(
            @RequestParam(value = "package", required = false, defaultValue = "") String packageName,
            @RequestParam(value = "card", required = false, defaultValue = "") String cardName,
            @RequestParam(value = "rare", required = false, defaultValue = "") String rare,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "pagesize", required = false, defaultValue = "20") int pageSize
    ) throws Exception {
        List<CardListDTO> dtoList = cardService.selectListUser(packageName, cardName, rare);
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
    @RequestMapping("/weird_project/card/ownlist")
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
     * 【ALL】查询卡片更改的历史纪录
     *
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param page        页码
     * @param pageSize    页面大小
     * @return 查询结果
     */
    @RequestMapping("/weird_project/card/history")
    public PageResult<CardHistoryDTO> searchHistory(
            @RequestParam(value = "package", required = false, defaultValue = "") String packageName,
            @RequestParam(value = "card", required = false, defaultValue = "") String cardName,
            @RequestParam(value = "rare", required = false, defaultValue = "") String rare,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "pagesize", required = false, defaultValue = "20") int pageSize
    ) throws Exception {
        List<CardHistoryDTO> dtoList = cardService.selectHistory(packageName, cardName, rare);
        PageResult<CardHistoryDTO> result = new PageResult<>();
        result.addPageInfo(dtoList, page, pageSize);
        return result;
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
    @GetMapping("/weird_project/card/add")
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
     * 【管理端】批量添加卡片信息
     *
     * @param param 信息参数
     * @return 是否添加成功
     */
    @PostMapping("/weird_project/card/add")
    public String addCardList(@RequestBody BatchAddCardParam param) throws OperationException {
        // 管理权限验证
        if (userService.checkLogin(param.getName(), param.getPassword()) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }

        if (param.getPackageName() == null || param.getPackageName().length() == 0) {
            throw new OperationException("卡包名为空！");
        }

        // 空参数判断
        StringBuilder sb = new StringBuilder();
        if (param.getNList() == null) {
            sb.append("N卡列表为NULL！\n");
        }
        if (param.getRList() == null) {
            sb.append("R卡列表为NULL！\n");
        }
        if (param.getSrList() == null) {
            sb.append("SR卡列表为NULL！\n");
        }
        if (param.getUrList() == null) {
            sb.append("UR卡列表为NULL！");
        }
        if (param.getHrList() == null) {
            sb.append("HR卡列表为NULL！");
        }
        if (sb.length() > 0) {
            throw new OperationException(sb.toString());
        }
        for (String nCard : param.getNList()) {
            if (nCard == null || nCard.length() == 0) {
                throw new OperationException("N卡中存在卡片名字为空！");
            }
        }
        for (String rCard : param.getRList()) {
            if (rCard == null || rCard.length() == 0) {
                throw new OperationException("R卡中存在卡片名字为空！");
            }
        }
        for (String srCard : param.getSrList()) {
            if (srCard == null || srCard.length() == 0) {
                throw new OperationException("SR卡中存在卡片名字为空！");
            }
        }
        for (String urCard : param.getUrList()) {
            if (urCard == null || urCard.length() == 0) {
                throw new OperationException("UR卡中存在卡片名字为空！");
            }
        }
        for (String hrCard : param.getUrList()) {
            if (hrCard == null || hrCard.length() == 0) {
                throw new OperationException("UR卡中存在卡片名字为空！");
            }
        }

        // 重名判断
        List<String> allCardList = new LinkedList<>();
        allCardList.addAll(param.getNList());
        allCardList.addAll(param.getRList());
        allCardList.addAll(param.getSrList());
        allCardList.addAll(param.getUrList());
        allCardList.addAll(param.getHrList());
        if (allCardList.size() == 0) {
            throw new OperationException("添加卡片列表为空！");
        }
        Set<String> allCardSet = new HashSet<>();
        List<String> dumpList = new LinkedList<>();
        for (String card : allCardList) {
            int oldCount = allCardSet.size();
            allCardSet.add(card);
            if (oldCount == allCardSet.size()) {
                dumpList.add(card);
            }
        }
        if (dumpList.size() > 0) {
            throw new OperationException("输入中存在重复卡片：%s", dumpList.toString());
        }

        // 添加卡片
        String result = packageService.addCardList(param, allCardList);
        if (result.length() == 0) {
            return "添加成功！";
        } else {
            throw new OperationException(result);
        }
    }

    /**
     * 【管理端】修改卡片名称
     *
     * @param oldCardName 旧卡片名
     * @param newCardName 新卡片名
     * @param isShow      是否在历史记录中显示该卡片
     * @param name        操作用户名称
     * @param password    操作用户密码
     * @return 是否修改成功
     */
    @RequestMapping("/weird_project/card/update")
    public String updateCardName(
            @RequestParam(value = "oldname") String oldCardName,
            @RequestParam(value = "newname") String newCardName,
            @RequestParam(value = "show", required = false, defaultValue = "0") int isShow,
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

        if (packageService.updateCardName(oldCardName, newCardName, isShow)) {
            return "修改成功！";
        } else {
            throw new OperationException("修改失败！");
        }
    }

    /**
     * 交换两张卡的稀有度
     *
     * @param cardName1 卡名1
     * @param cardName2 卡名2
     * @param isShow    是否展示
     * @param name      操作用户名称
     * @param password  操作用户密码
     * @return 是否修改成功
     * @throws Exception
     */
    @RequestMapping("/weird_project/card/exchange")
    public String exchangeName(
            @RequestParam(value = "card1") String cardName1,
            @RequestParam(value = "card2") String cardName2,
            @RequestParam(value = "show", required = false, defaultValue = "0") int isShow,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }
        if (cardName1 == null || cardName1.length() == 0
                || cardName2 == null || cardName2.length() == 0) {
            throw new OperationException("卡片名为空！");
        }

        if (packageService.exchangeCardName(cardName1, cardName2, isShow)) {
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
