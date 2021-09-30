package com.weird.controller;

import com.alibaba.fastjson.JSONObject;
import com.weird.aspect.SearchParamFix;
import com.weird.aspect.TrimArgs;
import com.weird.facade.BroadcastFacade;
import com.weird.handler.ChatHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * QQ聊天相关
 *
 * @author Nidhogg
 */
@RestController
@TrimArgs
@SearchParamFix
@Slf4j
public class ChatController {
    @Autowired
    List<ChatHandler> chatHandlerList;

    @RequestMapping("/ping")
    public void ping(@RequestBody JSONObject o) {
        try {
            String message = o.getString("raw_message");
            for (ChatHandler chatHandler : chatHandlerList) {
                chatHandler.handle(message);
            }
        } catch (Exception e) {
            log.error(o.toJSONString(), e);
        }
    }
}
