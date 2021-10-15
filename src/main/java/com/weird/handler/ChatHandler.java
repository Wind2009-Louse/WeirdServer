package com.weird.handler;

import com.alibaba.fastjson.JSONObject;

import java.util.regex.Pattern;

/**
 * 处理聊天信息的处理器
 *
 * @author Nidhogg
 * @date 2021.9.30
 */
public interface ChatHandler {
    Pattern PAGE_PATTERN = Pattern.compile("([\\S\\s]+) (\\d+)");

    /**
     * 处理聊天内容
     *
     * @param o 聊天内容
     */
    void handle(JSONObject o) throws Exception;
}
