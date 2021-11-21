package com.weird.controller;

import com.alibaba.fastjson.JSONObject;
import com.weird.aspect.SearchParamFix;
import com.weird.aspect.TrimArgs;
import com.weird.facade.BroadcastFacade;
import com.weird.handler.ChatHandler;
import com.weird.utils.ResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static com.weird.utils.BroadcastUtil.MESSAGE;
import static com.weird.utils.BroadcastUtil.buildResponse;

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

    @Autowired
    BroadcastFacade broadcastFacade;

    @RequestMapping("/ping")
    @ResponseBody
    public void ping(HttpServletRequest request) throws IOException {
        String str = (new BufferedReader( new InputStreamReader(request.getInputStream(), "UTF-8"))).readLine();
        log.info(str);
        JSONObject o = JSONObject.parseObject(str);
        try {
            if (o.containsKey(MESSAGE)) {
                String rawMessage = o.getString(MESSAGE);
                if (rawMessage.startsWith("＞")) {
                    o.put(MESSAGE, rawMessage.replaceFirst("＞", ">"));
                } else if (rawMessage.startsWith("》")) {
                    o.put(MESSAGE, rawMessage.replaceFirst("》", ">"));
                }
            }
            for (ChatHandler chatHandler : chatHandlerList) {
                chatHandler.handle(o);
            }
        } catch (ResponseException responseException) {
            broadcastFacade.sendMsgAsync(buildResponse(responseException.getMessage(), o));
        } catch (Exception e) {
            log.error(o.toJSONString(), e);
        }
    }
}
