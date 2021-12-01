package com.weird.handler.chatimpl;

import com.alibaba.fastjson.JSONObject;
import com.weird.config.AutoConfig;
import com.weird.config.DuelConfig;
import com.weird.facade.BroadcastFacade;
import com.weird.handler.ChatHandler;
import com.weird.model.dto.UserDataDTO;
import com.weird.model.enums.LoginTypeEnum;
import com.weird.service.UserService;
import com.weird.utils.StringExtendUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import static com.weird.utils.BroadcastUtil.*;

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

    @Autowired
    DuelConfig duelConfig;

    final static List<String> SPLIT_LIST = Arrays.asList(">认证 ", ">绑定 ", "＞认证 ", "＞绑定 ");

    final static List<String> SPLIT_STR_INFO_LIST = Arrays.asList(">信息", ">查询", ">查信息");

    final static String SPLIT_UNBIND = ">解绑";

    @Override
    public void handle(JSONObject o) {
        String message = o.getString(MESSAGE);
        String userQQ = o.getString(QQ);
        for (String splitString : SPLIT_LIST) {
            if (message.startsWith(splitString)) {
                String args = message.substring(splitString.length()).trim();
                if (StringUtils.isEmpty(args)) {
                    return;
                }

                List<String> argList = StringExtendUtil.split(args, " ");
                if (argList.size() != 2) {
                    broadcastFacade.sendMsgAsync(buildResponse("请输入正确的格式：\n>认证 用户名 密码", o));
                    return;
                }
                String userName = argList.get(0);
                String rawPassword = argList.get(1);
                String password = DigestUtils.md5DigestAsHex(rawPassword.getBytes());

                if (userService.checkLogin(userName, password) == LoginTypeEnum.UNLOGIN) {
                    broadcastFacade.sendMsgAsync(buildResponse("用户名或密码错误，请重新输入！", o));
                } else {
                    try {
                        boolean updateResult = userService.updateQQ(userName, userQQ);
                        if (updateResult) {
                            String result = String.format("你已成功绑定帐号[%s]！", userName);
                            broadcastFacade.sendMsgAsync(buildResponse(result, o, true));
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        broadcastFacade.sendMsgAsync(buildResponse("更新用户信息失败，请联系管理员", o));
                    }
                }

                return;
            }
        }

        for (String splitStrInfo : SPLIT_STR_INFO_LIST) {
            if (message.startsWith(splitStrInfo)) {
                String args = message.substring(splitStrInfo.length()).trim();
                List<String> argList = StringExtendUtil.split(args, " ");
                UserDataDTO targetData = userService.getUserByQQ(userQQ);
                String nullWarning = NOT_BIND_WARNING;
                if (targetData != null && userService.adminCheck(targetData.getUserName()) && !CollectionUtils.isEmpty(argList)) {
                    userQQ = argList.get(0);
                    Matcher matcher = AT_PATTERN.matcher(userQQ);
                    if (matcher.matches()) {
                        userQQ = matcher.group(1);
                    }
                    targetData = userService.getUserByQQ(userQQ);
                    nullWarning = "该用户暂未绑定账户！";
                }
                broadcastFacade.sendMsgAsync(buildResponse(printUserInfo(targetData, nullWarning), o, true));
                return;
            }
        }

        String response = "";
        if (SPLIT_UNBIND.equals(message)) {
            UserDataDTO userData = userService.getUserByQQ(userQQ);
            if (userData == null) {
                response += NOT_BIND_WARNING;
            } else {
                if (userService.unbindQQ(userQQ)) {
                    response += String.format("已解除和[%s]的绑定关系！", userData.getUserName());
                } else {
                    response = "解除绑定失败！";
                }
            }
        }
        if (!StringUtils.isEmpty(response)) {
            broadcastFacade.sendMsgAsync(buildResponse(response, o, true));
        }
    }

    public String printUserInfo(UserDataDTO userData, String nullWarning) {
        if (userData == null) {
            return nullWarning;
        } else {
            String result = String.format("\n绑定用户：%s\n尘：%d\n硬币：%d\n月见黑：%d",
                    userData.getUserName(),
                    userData.getDustCount(),
                    userData.getCoin(),
                    userData.getNonawardCount());

            if (AutoConfig.fetchDp()) {
                result += String.format("\nDP：%d", userData.getDuelPoint());
            }

            result += String.format("\n转盘次数：%d\n抽卡计数：%d/50",
                    userData.getRoulette(),
                    userData.getRollCount());

            return result;
        }
    }
}