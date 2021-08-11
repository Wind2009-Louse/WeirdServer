package com.weird.controller;

import com.weird.aspect.SearchParamFix;
import com.weird.aspect.TrimArgs;
import com.weird.model.CardPreviewModel;
import com.weird.model.dto.*;
import com.weird.model.enums.LoginTypeEnum;
import com.weird.model.param.BatchAddCardParam;
import com.weird.model.param.ReplaceCardParam;
import com.weird.model.param.SearchCardParam;
import com.weird.model.param.SearchHistoryParam;
import com.weird.service.CardPreviewService;
import com.weird.service.CardService;
import com.weird.service.PackageService;
import com.weird.service.UserService;
import com.weird.utils.CardPreviewUtil;
import com.weird.utils.OperationException;
import com.weird.utils.PackageUtil;
import com.weird.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 卡片列表相关
 *
 * @author Nidhogg
 */
@RestController
@TrimArgs
@SearchParamFix
public class CardListController {
    @Autowired
    UserService userService;
    @Autowired
    PackageService packageService;
    @Autowired
    CardService cardService;
    @Autowired
    CardPreviewService cardPreviewService;

    /**
     * 【ALL】全卡片搜索
     *
     * @param param 参数
     * @return 搜索结果
     */
    @RequestMapping("/weird_project/card/list")
    public PageResult<CardListDTO> searchCardList(@RequestBody SearchCardParam param) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(param.getName(), param.getPassword()) != LoginTypeEnum.ADMIN) {
            return searchCardListUser(param);
        } else {
            return searchCardListAdmin(param);
        }
    }

    /**
     * 【管理端】全卡片搜索
     *
     * @param param 参数
     * @return 搜索结果
     */
    @RequestMapping("/weird_project/card/list/admin")
    public PageResult<CardListDTO> searchCardListAdmin(@RequestBody SearchCardParam param) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(param.getName(), param.getPassword()) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }
        List<String> cardList = cardPreviewService.blurSearch(param.getCardName());
        List<CardListDTO> dtoList = cardService.selectListAdmin(param, cardList);
        PageResult<CardListDTO> result = new PageResult<>();
        result.addPageInfo(dtoList, param.getPage(), param.getPageSize());
        if (param.getPageSize() < CardPreviewUtil.HIDE_PREVIEW_COUNT) {
            for (CardListDTO data : result.getDataList()) {
                CardPreviewModel preview = cardPreviewService.selectPreviewByName(data.getCardName());
                if (preview != null) {
                    data.setDesc(CardPreviewUtil.getPreview(preview));
                    data.setPicId(preview.getId());
                }
            }
        }
        return result;
    }

    /**
     * 【玩家端】全已知卡片搜索
     *
     * @param param 参数
     * @return 搜索结果
     */
    @RequestMapping("/weird_project/card/list/user")
    public PageResult<CardListDTO> searchCardListUser(@RequestBody SearchCardParam param) throws Exception {
        List<String> cardList = cardPreviewService.blurSearch(param.getCardName());
        List<CardListDTO> dtoList = cardService.selectListUser(param, cardList);
        PageResult<CardListDTO> result = new PageResult<>();
        result.addPageInfo(dtoList, param.getPage(), param.getPageSize());
        if (param.getPageSize() < CardPreviewUtil.HIDE_PREVIEW_COUNT) {
            for (CardListDTO data : result.getDataList()) {
                CardPreviewModel preview = cardPreviewService.selectPreviewByName(data.getCardName());
                if (preview != null) {
                    data.setDesc(CardPreviewUtil.getPreview(preview));
                    data.setPicId(preview.getId());
                }
            }
        }
        return result;
    }

    /**
     * 【ALL】全卡片拥有情况搜索
     *
     * @param param 参数
     * @return 搜索结果
     */
    @RequestMapping("/weird_project/card/ownlist")
    public PageResult<CardOwnListDTO> searchCardOwnList(@RequestBody SearchCardParam param) throws Exception {
        List<String> cardList = cardPreviewService.blurSearch(param.getCardName());
        List<CardOwnListDTO> dtoList = cardService.selectList(param, cardList);
        PageResult<CardOwnListDTO> result = new PageResult<>();
        result.addPageInfo(dtoList, param.getPage(), param.getPageSize());
        if (param.getPageSize() < CardPreviewUtil.HIDE_PREVIEW_COUNT) {
            for (CardOwnListDTO data : result.getDataList()) {
                CardPreviewModel preview = cardPreviewService.selectPreviewByName(data.getCardName());
                if (preview != null) {
                    data.setDesc(CardPreviewUtil.getPreview(preview));
                    data.setPicId(preview.getId());
                }
            }
        }
        return result;
    }

    /**
     * 【ALL】查询卡片更改的历史纪录
     *
     * @param param 参数
     * @return 查询结果
     */
    @RequestMapping("/weird_project/card/history")
    public PageResult<CardHistoryDTO> searchHistory(@RequestBody SearchHistoryParam param) throws Exception {
        List<String> nameList = cardPreviewService.blurSearch(param.getCardName());
        List<CardHistoryDTO> dtoList = cardService.selectHistory(param, nameList);
        PageResult<CardHistoryDTO> result = new PageResult<>();
        result.addPageInfo(dtoList, param.getPage(), param.getPageSize());
        if (param.getPageSize() < CardPreviewUtil.HIDE_PREVIEW_COUNT) {
            for (CardHistoryDTO data : result.getDataList()) {
                CardPreviewModel oldPreview = cardPreviewService.selectPreviewByName(data.getOldName());
                CardPreviewModel newPreview = cardPreviewService.selectPreviewByName(data.getNewName());
                if (oldPreview != null) {
                    data.setOldDesc(CardPreviewUtil.getPreview(oldPreview));
                    data.setOldPicId(oldPreview.getId());
                }
                if (newPreview != null) {
                    data.setNewDesc(CardPreviewUtil.getPreview(newPreview));
                    data.setNewPicId(newPreview.getId());
                }
            }
        }
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
        if (StringUtils.isEmpty(cardName)) {
            throw new OperationException("卡片名为空！");
        }

        // 稀有度验证
        if (!PackageUtil.RARE_LIST.contains(rare)) {
            throw new OperationException("稀有度设置错误！");
        }

        if (packageService.addCard(packageName, cardName, rare, name)) {
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

        if (StringUtils.isEmpty(param.getPackageName())) {
            throw new OperationException("卡包名为空！");
        }

        // 空参数判断
        StringBuilder sb = new StringBuilder();
        Map<String, List<String>> checkList = new LinkedHashMap<>();
        checkList.put("N卡", param.getNList());
        checkList.put("R卡", param.getRList());
        checkList.put("SR卡", param.getSrList());
        checkList.put("UR卡", param.getUrList());
        checkList.put("HR卡", param.getHrList());
        checkList.put("GR卡", param.getGrList());
        checkList.put("SER卡", param.getSerList());
        for (Map.Entry<String, List<String>> entry : checkList.entrySet()) {
            if (entry.getValue() == null) {
                sb.append(String.format("%s列表为NULL!", entry.getKey()));
                continue;
            }
            for (String card : entry.getValue()) {
                if (StringUtils.isEmpty(card)) {
                    sb.append(String.format("%s中存在卡片名字为空！\n", entry.getKey()));
                    break;
                }
            }
        }
        if (sb.length() > 0) {
            throw new OperationException(sb.toString());
        }

        // 重名判断
        List<String> allCardList = new LinkedList<>();
        allCardList.addAll(param.getNList());
        allCardList.addAll(param.getRList());
        allCardList.addAll(param.getSrList());
        allCardList.addAll(param.getUrList());
        allCardList.addAll(param.getHrList());
        allCardList.addAll(param.getGrList());
        allCardList.addAll(param.getSerList());
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
     * @param newRare     新稀有度
     * @param isShow      是否在历史记录中显示该卡片
     * @param name        操作用户名称
     * @param password    操作用户密码
     * @return 是否修改成功
     */
    @RequestMapping("/weird_project/card/update")
    public String updateCardName(
            @RequestParam(value = "oldname") String oldCardName,
            @RequestParam(value = "newname") String newCardName,
            @RequestParam(value = "newRare") String newRare,
            @RequestParam(value = "needCoin", required = false, defaultValue = "-1") int needCoin,
            @RequestParam(value = "show", required = false, defaultValue = "0") int isShow,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "password") String password) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(name, password) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }

        if (packageService.updateCardName(oldCardName, newCardName, newRare, needCoin, isShow, name)) {
            return "修改成功！";
        } else {
            throw new OperationException("修改失败！");
        }
    }

    /**
     * 【管理端】交换两张卡的稀有度
     *
     * @param cardName1 卡名1
     * @param cardName2 卡名2
     * @param isShow    是否展示
     * @param name      操作用户名称
     * @param password  操作用户密码
     * @return 是否修改成功
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
        if (StringUtils.isEmpty(cardName1) || StringUtils.isEmpty(cardName2)) {
            throw new OperationException("卡片名为空！");
        }

        if (packageService.exchangeCardName(cardName1, cardName2, isShow, name)) {
            return "修改成功！";
        } else {
            throw new OperationException("修改失败！");
        }
    }

    @RequestMapping("/weird_project/card/exchangeOwn")
    public String exchangeUserCard(@RequestBody ReplaceCardParam param) throws Exception {
        if (param.getCount() == 0) {
            param.setCount(1);
        }
        if (param.getCount() < 0) {
            throw new OperationException("不能替换负数张！");
        }
        if (StringUtils.isEmpty(param.getTargetUser())) {
            throw new OperationException("操作对象为空！");
        }
        if (Objects.equals(param.getOldCardName(), param.getNewCardName())) {
            throw new OperationException("卡片没有修改！");
        }

        // 管理权限验证
        if (userService.checkLogin(param.getName(), param.getPassword()) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }

        return userService.exchangeOwnCard(param);
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
