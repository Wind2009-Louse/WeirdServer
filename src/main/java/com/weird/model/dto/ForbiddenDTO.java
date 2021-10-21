package com.weird.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 禁限表
 *
 * @author Nidhogg
 * @date 2021.10.21
 */
@Data
public class ForbiddenDTO implements Serializable {
    /**
     * 限制
     */
    ForbiddenListDTO limit;

    /**
     * 准限制
     */
    ForbiddenListDTO semiLimit;
}
