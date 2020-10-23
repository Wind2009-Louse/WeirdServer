package com.weird.aspect;

import com.weird.model.dto.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;


/**
 * 对{@link SearchCardParam}和{@link SearchHistoryParam}的null参数进行修复
 *
 * @author Nidhogg
 * @date 2020.10.23
 */
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface SearchParamFix {
}
