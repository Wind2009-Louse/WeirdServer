package com.weird.handler.chatimpl;

import com.alibaba.fastjson.JSONObject;
import com.weird.facade.BroadcastFacade;
import com.weird.handler.ChatHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.weird.utils.BroadcastUtil.MESSAGE;
import static com.weird.utils.BroadcastUtil.buildResponse;

/**
 * ack应答
 *
 * @author Nidhogg
 * @date 2021.9.30
 */
@Component
public class ChatAckHandler implements ChatHandler {
    @Autowired
    BroadcastFacade broadcastFacade;

    final static String SYN = "syn";
    final static String ACK = "ack";

    @Override
    public void handle(JSONObject o) {
        String message = o.getString(MESSAGE);
        if (SYN.equals(message)) {
            broadcastFacade.sendMsgAsync(buildResponse(ACK, o));
        }
    }
}
