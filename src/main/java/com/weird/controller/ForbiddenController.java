package com.weird.controller;

import com.weird.aspect.SearchParamFix;
import com.weird.aspect.TrimArgs;
import com.weird.model.CardPreviewModel;
import com.weird.model.ForbiddenModel;
import com.weird.model.PackageInfoModel;
import com.weird.model.dto.ForbiddenDTO;
import com.weird.model.dto.ForbiddenInfoDTO;
import com.weird.model.dto.ForbiddenListDTO;
import com.weird.model.enums.LoginTypeEnum;
import com.weird.model.param.ForbiddenUpdateParam;
import com.weird.service.CardPreviewService;
import com.weird.service.ForbiddenService;
import com.weird.service.UserService;
import com.weird.utils.BeanConverter;
import com.weird.utils.CardPreviewUtil;
import com.weird.utils.OperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 禁限Controller
 *
 * @author Nidhogg
 * @date 2021.10.21
 */
@RestController
@TrimArgs
@SearchParamFix
@Slf4j
public class ForbiddenController {
    @Autowired
    ForbiddenService forbiddenService;

    @Autowired
    CardPreviewService cardPreviewService;

    @Autowired
    UserService userService;

    /**
     * 获取禁限表信息
     *
     * @return
     */
    @RequestMapping("/weird_project/forbidden/list")
    public ForbiddenDTO getForbidden() {
        List<ForbiddenModel> forbiddenList = forbiddenService.selectAll();
        Map<Integer, List<ForbiddenModel>> forbiddenMap = forbiddenList.stream().collect(Collectors.groupingBy(ForbiddenModel::getCount));
        List<ForbiddenModel> limitDataList = forbiddenMap.getOrDefault(1, Collections.emptyList());
        List<ForbiddenModel> semiLimitDataList = forbiddenMap.getOrDefault(2, Collections.emptyList());
        List<ForbiddenInfoDTO> limitDTOList = BeanConverter.convertList(limitDataList, ForbiddenInfoDTO.class);
        List<ForbiddenInfoDTO> semiLimitDTOList = BeanConverter.convertList(semiLimitDataList, ForbiddenInfoDTO.class);

        List<ForbiddenInfoDTO> allDTOList = new LinkedList<>();
        allDTOList.addAll(limitDTOList);
        allDTOList.addAll(semiLimitDTOList);
        for (ForbiddenInfoDTO dto : allDTOList) {
            if (dto.getCode() > 0) {
                CardPreviewModel preview = cardPreviewService.selectPreviewByCode(dto.getCode());
                if (preview != null) {
                    dto.setName(preview.getName());
                    dto.setDetail(CardPreviewUtil.getPreview(preview));
                    continue;
                }
            }
            if (!StringUtils.isEmpty(dto.getName())) {
                CardPreviewModel preview = cardPreviewService.selectPreviewByName(dto.getName());
                if (preview != null) {
                    dto.setCode(preview.getId());
                    dto.setDetail(CardPreviewUtil.getPreview(preview));
                    continue;
                }
            }
        }

        ForbiddenListDTO limitData = new ForbiddenListDTO(limitDTOList);
        ForbiddenListDTO semiLimitData = new ForbiddenListDTO(semiLimitDTOList);
        ForbiddenDTO result = new ForbiddenDTO();
        result.setLimit(limitData);
        result.setSemiLimit(semiLimitData);
        return result;
    }

    @RequestMapping("/weird_project/forbidden/update")
    public String getForbidden(@RequestBody ForbiddenUpdateParam param) throws OperationException {
        // 管理权限验证
        if (userService.checkLogin(param.getName(), param.getPassword()) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }

        ForbiddenDTO data = param.getData();
        if (data == null || data.getLimit() == null || data.getSemiLimit() == null) {
            throw new OperationException("参数为空！");
        }
        data.getLimit().buildInfo(cardPreviewService);
        data.getSemiLimit().buildInfo(cardPreviewService);

        List<String> limitNameList = data.getLimit().getInfoList().stream().map(ForbiddenInfoDTO::getName).collect(Collectors.toList());
        List<String> semiLimitNameList = data.getSemiLimit().getInfoList().stream().map(ForbiddenInfoDTO::getName).collect(Collectors.toList());
        limitNameList.retainAll(semiLimitNameList);
        if (!CollectionUtils.isEmpty(limitNameList)) {
            throw new OperationException("以下卡片重复出现：[%s]",String.join(",", limitNameList));
        }

        if (forbiddenService.update(data, param.getName())) {
            return "修改成功！";
        } else {
            throw new OperationException("修改失败！");
        }
    }
}

