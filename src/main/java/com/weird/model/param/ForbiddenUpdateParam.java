package com.weird.model.param;

import com.weird.model.dto.ForbiddenDTO;
import lombok.Data;

/**
 * 禁限表更新参数
 *
 * @author Nidhogg
 * @date 2021.10.21
 */
@Data
public class ForbiddenUpdateParam extends UserCheckParam {
    ForbiddenDTO data;
}
