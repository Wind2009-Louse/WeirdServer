package com.weird.controller;

import com.weird.aspect.SearchParamFix;
import com.weird.aspect.TrimArgs;
import com.weird.model.CardPreviewModel;
import com.weird.model.dto.*;
import com.weird.model.enums.LoginTypeEnum;
import com.weird.model.param.*;
import com.weird.service.CardPreviewService;
import com.weird.service.CardService;
import com.weird.service.DeckService;
import com.weird.service.UserService;
import com.weird.utils.BeanConverter;
import com.weird.utils.CardPreviewUtil;
import com.weird.utils.OperationException;
import com.weird.utils.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.management.openmbean.OpenDataException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 卡组相关
 *
 * @author Nidhogg
 * @date 2021.8.14
 */
@RestController
@TrimArgs
@SearchParamFix
@Slf4j
public class DeckController {
    @Autowired
    DeckService deckService;

    @Autowired
    UserService userService;

    @Autowired
    CardService cardService;

    @Autowired
    CardPreviewService cardPreviewService;

    /**
     * 【ALL】搜索卡组列表
     *
     * @param param 参数
     * @return 搜索结果
     */
    @RequestMapping("/weird_project/deck/list")
    public PageResult<DeckListDTO> searchCardList(@RequestBody DeckListParam param) throws Exception {
        // 管理权限验证
        List<DeckListDTO> resultList;
        if (userService.checkLogin(param.getName(), param.getPassword()) != LoginTypeEnum.ADMIN) {
            if (param.getShare() <= 0) {
                param.setTargetUser(param.getName());
            }
            resultList = deckService.searchPage(param);
        } else {
            resultList = deckService.searchPageAdmin(param);
        }
        PageResult<DeckListDTO> result = new PageResult<>();
        result.addPageInfo(resultList, param.getPage(), param.getPageSize());
        return result;
    }

    /**
     * 【ALL】添加卡组
     *
     * @param param
     * @return
     * @throws Exception
     */
    @RequestMapping("/weird_project/deck/add")
    public String addDeck(@RequestBody DeckSubmitParam param) throws Exception {
        if (userService.checkLogin(param.getName(), param.getPassword()) == LoginTypeEnum.UNLOGIN) {
            throw new OperationException("你未登录！");
        }
        // 如果有YDK，根据YDK组建卡组列表
        DeckInfoDTO deck = param.getDeck();
        deck.buildDeckList();

        // 判断卡组是否合法
        if (!deck.checkDeck()) {
            throw new OperationException("卡片数量有错，请重新检查后上传！");
        }
        if (deckService.addDeck(param)) {
            return "添加成功！";
        } else {
            throw new OperationException("添加失败！");
        }
    }

    /**
     * 【ALL】添加卡组
     *
     * @param param
     * @return
     * @throws Exception
     */
    @RequestMapping("/weird_project/deck/add/batch")
    public String addDeckBatch(@RequestBody DeckSubmitBatchParam param) throws Exception {
        if (userService.checkLogin(param.getName(), param.getPassword()) == LoginTypeEnum.UNLOGIN) {
            throw new OperationException("你未登录！");
        }

        int successCount = 0;
        for (DeckInfoDTO deck : param.getDeckList()) {
            try {
                DeckSubmitParam submitParam = BeanConverter.convert(param, DeckSubmitParam.class);

                // 如果有YDK，根据YDK组建卡组列表
                deck.buildDeckList();
                // 判断卡组是否合法
                if (!deck.checkDeck()) {
                    continue;
                }
                submitParam.setDeck(deck);

                if (deckService.addDeck(submitParam)) {
                    successCount += 1;
                }
            } catch (OperationException e) {
                log.error("批量上传卡组错误：{}", e.getMessage());
            }
        }

        return String.format("成功上传%d个卡组！", successCount);
    }

    /**
     * 【ALL】修改卡组
     *
     * @param param
     * @return
     * @throws Exception
     */
    @RequestMapping("/weird_project/deck/update")
    public String updateDeck(@RequestBody DeckSubmitParam param) throws Exception {
        if (userService.checkLogin(param.getName(), param.getPassword()) == LoginTypeEnum.UNLOGIN) {
            throw new OpenDataException("你未登录！");
        }
        // 如果有YDK，根据YDK组建卡组列表
        DeckInfoDTO deck = param.getDeck();
        deck.buildDeckList();

        // 判断卡组是否合法
        if (!deck.checkDeck()) {
            throw new OperationException("卡片数量有错，请重新检查后上传！");
        }
        if (deckService.updateDeck(param)) {
            return "修改成功！";
        } else {
            throw new OperationException("修改失败！");
        }
    }

    /**
     * 【ALL】查看卡组详情
     *
     * @param param 参数
     * @return
     * @throws Exception
     */
    @RequestMapping("/weird_project/deck/info")
    public DeckInfoDTO deckDetail(@RequestBody DeckInfoParam param) throws Exception {
        LoginTypeEnum loginTypeEnum = userService.checkLogin(param.getName(), param.getPassword());
        if (loginTypeEnum == LoginTypeEnum.UNLOGIN) {
            throw new OperationException("你未登录！");
        }
        boolean isAdmin = loginTypeEnum == LoginTypeEnum.ADMIN;

        DeckInfoDTO deckInfo = deckService.getDeckInfo(param, true);
        String checkOwnUser = deckInfo.getUserName();
        // 不是卡组持有者、不是分享中的卡组、不是管理员，不能查看卡组
        if (!Objects.equals(checkOwnUser, param.getName()) && !isAdmin) {
            if (deckInfo.getShare() <= 0) {
                throw new OperationException("无权查看此卡组！");
            }
            checkOwnUser = param.getName();
        }
        boolean adminCheckSelf = Objects.equals(checkOwnUser, param.getName()) && isAdmin;

        List<DeckCardDTO> allCardList = new LinkedList<>();
        allCardList.addAll(deckInfo.getMainList());
        allCardList.addAll(deckInfo.getExList());
        allCardList.addAll(deckInfo.getSideList());
        
        // 获取卡片说明
        for (DeckCardDTO deckCard : allCardList) {
            long code = deckCard.getCode();
            CardPreviewModel preview = cardPreviewService.selectPreviewByCode(code);
            if (preview != null) {
                deckCard.setCardName(preview.getName());
                deckCard.setDesc(CardPreviewUtil.getPreview(preview));;
            }
        }

        // 获取用户持有数量
        List<String> nameList = allCardList.stream().map(DeckCardDTO::getCardName).collect(Collectors.toList());
        SearchCardParam countParam = new SearchCardParam();
        countParam.setName(checkOwnUser);
        List<CardListDTO> cardCountList = cardService.selectListUser(countParam, nameList);
        Map<String, CardListDTO> cardMap = cardCountList.stream().collect(Collectors.toMap(CardListDTO::getCardName, Function.identity()));
        for (DeckCardDTO card : allCardList) {
            CardListDTO cardData = cardMap.getOrDefault(card.getCardName(), null);
            if (cardData != null) {
                card.setPackageName(cardData.getPackageName());
                card.setRare(cardData.getRare());
                if (adminCheckSelf) {
                    card.setOwn(3);
                } else {
                    card.setOwn(cardData.getCount());
                }
            }
        }

        return deckInfo;
    }

    /**
     * 重命名卡组
     *
     * @param param 参数
     * @return
     * @throws Exception
     */
    @RequestMapping("/weird_project/deck/rename")
    public String renameDeck(@RequestBody DeckSubmitParam param) throws Exception {
        LoginTypeEnum loginTypeEnum = userService.checkLogin(param.getName(), param.getPassword());
        if (loginTypeEnum == LoginTypeEnum.UNLOGIN) {
            throw new OperationException("你未登录！");
        }
        if (StringUtils.isEmpty(param.getDeck().getDeckName())) {
            throw new OperationException("卡组名为空！");
        }

        if (deckService.renameDeck(param)) {
            return "修改成功！";
        } else {
            throw new OperationException("修改失败！");
        }
    }

    /**
     * 删除卡组
     *
     * @param param
     * @return
     * @throws Exception
     */
    @RequestMapping("/weird_project/deck/remove")
    public String removeDeck(@RequestBody DeckSubmitParam param) throws Exception {
        LoginTypeEnum loginTypeEnum = userService.checkLogin(param.getName(), param.getPassword());
        if (loginTypeEnum == LoginTypeEnum.UNLOGIN) {
            throw new OperationException("你未登录！");
        }

        if (deckService.removeDeck(param)) {
            return "删除成功！";
        } else {
            throw new OperationException("删除失败！");
        }
    }

    @RequestMapping("/weird_project/deck/share")
    public String shareDeck(@RequestBody DeckShareParam param) throws Exception {
        LoginTypeEnum loginTypeEnum = userService.checkLogin(param.getName(), param.getPassword());
        if (loginTypeEnum == LoginTypeEnum.UNLOGIN) {
            throw new OperationException("你未登录！");
        }

        return deckService.shareDeck(param, loginTypeEnum == LoginTypeEnum.ADMIN);
    }
}
