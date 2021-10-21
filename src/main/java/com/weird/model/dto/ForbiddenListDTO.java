package com.weird.model.dto;

import com.weird.model.CardPreviewModel;
import com.weird.service.CardPreviewService;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 限制信息
 *
 * @author Nidhogg
 * @date 2021.10.21
 */
@Data
public class ForbiddenListDTO implements Serializable {
    /**
     * 编号列表
     */
    List<Integer> codeList;

    /**
     * 卡名列表
     */
    List<String> nameList;

    /**
     * 信息列表
     */
    List<ForbiddenInfoDTO> infoList;

    public ForbiddenListDTO() {
        codeList = new LinkedList<>();
        nameList = new LinkedList<>();
        infoList = new LinkedList<>();
    }

    public ForbiddenListDTO(List<ForbiddenInfoDTO> list) {
        codeList = list.stream().map(ForbiddenInfoDTO::getCode).collect(Collectors.toList());
        nameList = list.stream().map(ForbiddenInfoDTO::getName).collect(Collectors.toList());
        infoList = list;
    }

    public void buildInfo(CardPreviewService service) {
        // 构建infoList
        if (CollectionUtils.isEmpty(infoList)) {
            infoList = new LinkedList<>();
            if (!CollectionUtils.isEmpty(codeList)) {
                for (Integer code : codeList) {
                    ForbiddenInfoDTO info = new ForbiddenInfoDTO();
                    info.setCode(code);
                    infoList.add(info);
                }
            } else if (!CollectionUtils.isEmpty(nameList)) {
                for (String name : nameList) {
                    ForbiddenInfoDTO info = new ForbiddenInfoDTO();
                    info.setName(name);
                    infoList.add(info);
                }
            }
        }

        // 补充infoList内容
        for (ForbiddenInfoDTO info : infoList) {
            CardPreviewModel preview = null;
            if (info.getCode() > 0 && StringUtils.isEmpty(info.getName())) {
                preview = service.selectPreviewByCode(info.getCode());
            }
            if (info.getCode() == 0 && !StringUtils.isEmpty(info.getName())) {
                preview = service.selectPreviewByName(info.getName());
            }
            if (preview != null) {
                info.setName(preview.getName());
                info.setCode(preview.getId());
            }
        }
    }
}
