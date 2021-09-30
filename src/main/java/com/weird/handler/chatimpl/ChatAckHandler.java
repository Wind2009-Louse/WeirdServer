package com.weird.handler.chatimpl;

import com.weird.facade.BroadcastFacade;
import com.weird.handler.ChatHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @Override
    public void handle(String message) {
        if ("syn".equals(message)) {
            broadcastFacade.sendMsgAsync("ack");
        }
    }
}
