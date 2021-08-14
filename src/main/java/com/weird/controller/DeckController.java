package com.weird.controller;

import com.weird.aspect.SearchParamFix;
import com.weird.aspect.TrimArgs;
import com.weird.model.dto.DeckInfoDTO;
import com.weird.model.dto.DeckListDTO;
import com.weird.model.enums.LoginTypeEnum;
import com.weird.model.param.DeckInfoParam;
import com.weird.model.param.DeckListParam;
import com.weird.model.param.DeckSubmitParam;
import com.weird.service.DeckService;
import com.weird.service.UserService;
import com.weird.utils.OperationException;
import com.weird.utils.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.management.openmbean.OpenDataException;
import java.util.List;

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
            throw new OpenDataException("你未登录！");
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

        return deckService.getDeckInfo(param, loginTypeEnum == LoginTypeEnum.ADMIN);
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
}
