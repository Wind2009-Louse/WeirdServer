package com.weird.controller;

import com.weird.aspect.SearchParamFix;
import com.weird.aspect.TrimArgs;
import com.weird.facade.RecordFacade;
import com.weird.model.RecordModel;
import com.weird.model.enums.LoginTypeEnum;
import com.weird.model.param.RecordParam;
import com.weird.service.UserService;
import com.weird.utils.OperationException;
import com.weird.utils.PageResult;
import com.weird.utils.StringExtendUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作记录相关
 *
 * @author Nidhogg
 */
@RestController
@TrimArgs
@SearchParamFix
public class RecordController {
    @Autowired
    RecordFacade recordFacade;

    @Autowired
    UserService userService;

    @PostMapping("/weird_project/record/list")
    public PageResult<RecordModel> searchPage(@RequestBody RecordParam param) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(param.getName(), param.getPassword()) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }
        param.setDetailList(StringExtendUtil.split(param.getDetail(), " "));

        return recordFacade.searchPage(param);
    }
}
