package com.weird.handler;

import com.weird.model.param.BlurSearchParam;

/**
 * 搜索的处理
 */
public interface CardSearchHandler {
    boolean handleParam(String realText, BlurSearchParam param);
}
