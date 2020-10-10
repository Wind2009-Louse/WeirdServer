package com.weird.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 将参数中的String自动进行Trim操作
 *
 * @author Nidhogg
 * @date 2020.10.10
 */
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface TrimArgs {
}
