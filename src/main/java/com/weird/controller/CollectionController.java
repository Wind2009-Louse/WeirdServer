package com.weird.controller;

import com.weird.aspect.SearchParamFix;
import com.weird.aspect.TrimArgs;
import com.weird.model.CardPreviewModel;
import com.weird.model.dto.CardListDTO;
import com.weird.model.enums.CollectionOperationEnum;
import com.weird.model.enums.LoginTypeEnum;
import com.weird.model.param.CollectionParam;
import com.weird.model.param.SearchCardParam;
import com.weird.model.param.UserCheckParam;
import com.weird.service.CardPreviewService;
import com.weird.service.CardService;
import com.weird.service.CollectionService;
import com.weird.service.UserService;
import com.weird.utils.CardPreviewUtil;
import com.weird.utils.OperationException;
import com.weird.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * 卡片收藏相关
 *
 * @author Nidhogg
 */
@RestController
@TrimArgs
@SearchParamFix
public class CollectionController {
    @Autowired
    UserService userService;

    @Autowired
    CollectionService collectionService;

    @Autowired
    CardService cardService;

    @Autowired
    CardPreviewService cardPreviewService;

    /**
     * 【ALL】获取收藏卡片ID
     *
     * @param param 参数
     * @return 搜索结果
     */
    @RequestMapping("/weird_project/collection/list")
    public List<Integer> searchCollectionIdList(@RequestBody UserCheckParam param) throws Exception {
        // 登录信息验证
        if (userService.checkLogin(param.getName(), param.getPassword()) == LoginTypeEnum.UNLOGIN) {
            return Collections.emptyList();
        } else {
            return collectionService.getCollectionByName(param.getName());
        }
    }

    /**
     * 【ALL】收藏操作
     *
     * @param param 参数
     * @return 搜索结果
     */
    @RequestMapping("/weird_project/collection/op")
    public boolean operation(@RequestBody CollectionParam param) throws OperationException {
        // 登录信息验证
        LoginTypeEnum loginTypeEnum = userService.checkLogin(param.getName(), param.getPassword());
        if (loginTypeEnum == LoginTypeEnum.UNLOGIN) {
            throw new OperationException("登录失败！");
        }

        if (CollectionOperationEnum.getById(param.getOp()) == null) {
            throw new OperationException("操作非法！");
        }

        return collectionService.operation(param, loginTypeEnum);
    }

    /**
     * 【ALL】查看收藏卡片列表
     *
     * @param param 参数
     * @return 搜索结果
     */
    @RequestMapping("/weird_project/collection/search")
    public PageResult<CardListDTO> searchCollectionCardList(@RequestBody CollectionParam param) throws Exception {
        // 登录信息验证
        final LoginTypeEnum loginTypeEnum = userService.checkLogin(param.getName(), param.getPassword());
        if (loginTypeEnum == LoginTypeEnum.UNLOGIN) {
            throw new OperationException("登录失败！");
        }

        // 查询卡片效果说明
        List<CardListDTO> dtoList = cardService.selectListCollection(param, loginTypeEnum);
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
}
