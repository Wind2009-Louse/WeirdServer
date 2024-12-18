package com.weird.controller;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONObject;
import com.weird.aspect.SearchParamFix;
import com.weird.aspect.TrimArgs;
import com.weird.facade.BroadcastFacade;
import com.weird.handler.ChatHandler;
import com.weird.utils.RequestUtil;
import com.weird.utils.ResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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
    public void ping(HttpServletRequest request) throws Exception {
        JSONObject o = RequestUtil.getJsonFromRequest(request);
        try {
            if (o.containsKey(MESSAGE)) {
                log.info(o.toJSONString());
                String rawMessage = o.getString(MESSAGE);
                if (rawMessage.startsWith("＞")) {
                    o.put(MESSAGE, rawMessage.replaceFirst("＞", ">"));
                } else if (rawMessage.startsWith("》")) {
                    o.put(MESSAGE, rawMessage.replaceFirst("》", ">"));
                }
                for (ChatHandler chatHandler : chatHandlerList) {
                    chatHandler.handle(o);
                }
            }
        } catch (ResponseException responseException) {
            broadcastFacade.sendMsgAsync(buildResponse(responseException.getMessage(), o));
        } catch (Exception e) {
            log.error(o.toJSONString(), e);
        }
    }
}
