package com.weird.handler.chatimpl;

import com.alibaba.fastjson.JSONObject;
import com.weird.facade.BroadcastFacade;
import com.weird.handler.ChatHandler;
import com.weird.model.dto.UserDataDTO;
import com.weird.model.enums.LoginTypeEnum;
import com.weird.service.RouletteService;
import com.weird.service.UserService;
import com.weird.utils.OperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.concurrent.ExecutionException;

import static com.weird.utils.BroadcastUtil.buildResponse;

/**
 * 转盘
 *
 * @author Nidhogg
 * @date 2021.9.30
 */
@Component
@Slf4j
public class ChatRouletteHandler implements ChatHandler {
    @Autowired
    BroadcastFacade broadcastFacade;

    @Autowired
    UserService userService;

    @Autowired
    RouletteService rouletteService;

    final static String SPLIT_STR = ">转盘";

    @Override
    public void handle(JSONObject o) {
        String message = o.getString("raw_message");
        String userQQ = o.getString("user_id");
        if (message.equals(SPLIT_STR)) {
            UserDataDTO userData = userService.getUserByQQ(userQQ);
            if (userData == null) {
                return;
            }

            try {
                rouletteService.roulette(userData.getUserName());
                broadcastFacade.sendMsgAsync(buildResponse("转盘中……", o));
            } catch (OperationException oe) {
                broadcastFacade.sendMsgAsync(buildResponse(oe.getMessage(), o));
            } catch (Exception e) {
                log.error(e.getMessage());
                broadcastFacade.sendMsgAsync(buildResponse("操作失败，请联系管理员", o));
            }
        }
    }
}