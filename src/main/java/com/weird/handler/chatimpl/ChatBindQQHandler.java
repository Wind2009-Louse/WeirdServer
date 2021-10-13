package com.weird.handler.chatimpl;

import com.alibaba.fastjson.JSONObject;
import com.weird.facade.BroadcastFacade;
import com.weird.handler.ChatHandler;
import com.weird.model.dto.UserDataDTO;
import com.weird.model.enums.LoginTypeEnum;
import com.weird.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

import static com.weird.utils.BroadcastUtil.buildResponse;

/**
 * 绑定QQ
 *
 * @author Nidhogg
 * @date 2021.10.12
 */
@Component
@Slf4j
public class ChatBindQQHandler implements ChatHandler {
    @Autowired
    BroadcastFacade broadcastFacade;

    @Autowired
    UserService userService;

    final static List<String> SPLIT_LIST = Arrays.asList(">认证 ", ">绑定 ");

    final static String SPLIT_STR_INFO = ">信息";

    final static String SPLIT_UNBIND = ">解绑";

    @Override
    public void handle(JSONObject o) {
        String message = o.getString("raw_message");
        String userQQ = o.getString("user_id");
        boolean inGroup = "group".equals(o.getString("message_type"));
        for (String splitString : SPLIT_LIST) {
            if (message.startsWith(splitString)) {
                String args = message.substring(splitString.length()).trim();
                if (StringUtils.isEmpty(args)) {
                    return;
                }

                String[] argList = args.split(" ");
                if (argList.length != 2) {
                    broadcastFacade.sendMsgAsync(buildResponse("请输入正确的格式：\n>认证 用户名 密码", o));
                }
                String userName = argList[0];
                String rawPassword = argList[1];
                String password = DigestUtils.md5DigestAsHex(rawPassword.getBytes());

                if (userService.checkLogin(userName, password) == LoginTypeEnum.UNLOGIN) {
                    broadcastFacade.sendMsgAsync(buildResponse("用户名或密码错误，请重新输入！", o));
                } else {
                    try {
                        boolean updateResult = userService.updateQQ(userName, userQQ);
                        if (updateResult) {
                            String result = String.format("你已成功绑定帐号[%s]！", userName);
                            if (inGroup) {
                                result = String.format("[CQ:at,qq=%s] ", userQQ) + result;
                            }
                            broadcastFacade.sendMsgAsync(buildResponse(result, o));
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        broadcastFacade.sendMsgAsync(buildResponse("更新用户信息失败，请联系管理员", o));
                    }
                }

                return;
            }
        }

        String response = "";
        if (SPLIT_STR_INFO.equals(message)) {
            if (inGroup) {
                response = String.format("[CQ:at,qq=%s] ", userQQ);
            }
            UserDataDTO userData = userService.getUserByQQ(userQQ);
            if (userData == null) {
                response += "你暂未绑定帐号，请私聊使用以下指令进行绑定！\n>认证 用户名 密码";
            } else {
                response += String.format("\n绑定用户：%s\n尘：%d\n月见黑：%d\n转盘次数：%d\n抽卡计数：%d/50",
                        userData.getUserName(),
                        userData.getDustCount(),
                        userData.getNonawardCount(),
                        userData.getRoulette(),
                        userData.getRollCount());
            }
        } else if (SPLIT_UNBIND.equals(message)) {
            UserDataDTO userData = userService.getUserByQQ(userQQ);
            if (userData == null) {
                response += "你暂未绑定帐号，请私聊使用以下指令进行绑定！\n>认证 用户名 密码";
            } else {
                if (userService.unbindQQ(userQQ)) {
                    if (inGroup) {
                        response = String.format("[CQ:at,qq=%s] ", userQQ);
                    }
                    response += String.format("已解除和[%s]的绑定关系！", userData.getUserName());
                } else {
                    response = "解除绑定失败！";
                }
            }
        }
        if (!StringUtils.isEmpty(response)) {
            broadcastFacade.sendMsgAsync(buildResponse(response, o));
        }
    }
}