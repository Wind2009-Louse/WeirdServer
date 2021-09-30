package com.weird.handler;

/**
 * 处理聊天信息的处理器
 *
 * @author Nidhogg
 * @date 2021.9.30
 */
public interface ChatHandler {
    /**
     * 处理聊天内容
     *
     * @param message
     */
    void handle(String message);
}
