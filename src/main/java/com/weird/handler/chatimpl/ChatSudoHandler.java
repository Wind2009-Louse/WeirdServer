package com.weird.handler.chatimpl;

import com.alibaba.fastjson.JSONObject;
import com.weird.facade.BroadcastFacade;
import com.weird.handler.ChatHandler;
import com.weird.model.dto.UserDataDTO;
import com.weird.service.UserService;
import com.weird.service.impl.UserServiceImpl;
import com.weird.utils.OperationException;
import com.weird.utils.StringExtendUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.weird.utils.BroadcastUtil.*;

/**
 * 管理员操作
 *
 * @author Nidhogg
 * @date 2021.11.25
 */
@Component
public class ChatSudoHandler implements ChatHandler {
    @Autowired
    BroadcastFacade broadcastFacade;

    @Autowired
    UserService userService;

    final static String SPLIT_STR = ">sudo ";

    static public Pattern ADD_PATTERN = Pattern.compile("\\+(\\d+)");

    static public Pattern SUB_PATTERN = Pattern.compile("-(\\d+)");

    static public Pattern EQ_PATTERN = Pattern.compile("=(\\d+)");

    @Override
    public void handle(JSONObject o) {
        String message = o.getString(MESSAGE);
        String userQQ = o.getString(QQ);
        if (message.startsWith(SPLIT_STR)) {
            UserDataDTO userData = userService.getUserByQQ(userQQ);
            if (userData == null || !userService.adminCheck(userData.getUserName())) {
                return;
            }
            String operator = userData.getUserName();

            String cardArgs = message.substring(SPLIT_STR.length()).trim();
            List<String> argList = StringExtendUtil.split(cardArgs, " ");
            if (argList.size() != 3) {
                return;
            }

            String target = argList.get(0);
            UserDataDTO targetUser;
            String operation = argList.get(1);
            String numberStr = argList.get(2);

            // 获取对象用户
            Matcher matcher = AT_PATTERN.matcher(target);
            if (matcher.matches()) {
                targetUser = userService.getUserByQQ(matcher.group(1));
            } else {
                targetUser = userService.getUserByName(target);
            }
            if (targetUser == null) {
                return;
            }
            String targetUserName = targetUser.getUserName();

            // 获取操作值
            Integer number = null;
            boolean setCount = false;
            Matcher addMatcher = ADD_PATTERN.matcher(numberStr);
            if (addMatcher.matches()) {
                number = Integer.parseInt(addMatcher.group(1));
            } else {
                Matcher subMatcher = SUB_PATTERN.matcher(numberStr);
                if (subMatcher.matches()) {
                    number = - Integer.parseInt(subMatcher.group(1));
                } else {
                    Matcher eqMatcher = EQ_PATTERN.matcher(numberStr);
                    if (eqMatcher.matches()) {
                        setCount = true;
                        number = Integer.parseInt(eqMatcher.group(1));
                    }
                }
            }
            if (number == null) {
                return;
            }

            // 进行操作
            String result = "";
            try {
                if (Arrays.asList("尘", "dust", "Dust", "DUST").contains(operation)) {
                    int newCount = setCount ? number : targetUser.getDustCount() + number;
                    result = userService.updateDust(targetUserName, newCount, operator);
                } else if (Arrays.asList("硬币", "coin", "Coin", "COIN").contains(operation)) {
                    int newCount = setCount ? number : targetUser.getCoin() + number;
                    result = userService.updateCoin(targetUserName, newCount, operator);
                } else if (Arrays.asList("月见黑", "黑", "award", "noaward", "nonaward").contains(operation)) {
                    int newCount = setCount ? number : targetUser.getNonawardCount() + number;
                    result = userService.updateAward(targetUserName, newCount, operator);
                } else if (Arrays.asList("DP", "dp", "Dp").contains(operation)) {
                    int newCount = setCount ? number : targetUser.getDuelPoint() + number;
                    result = userService.updateDuelPoint(targetUserName, newCount, operator);
                } else if (Arrays.asList("转盘", "转盘次数").contains(operation)) {
                    int newCount = setCount ? number : targetUser.getRoulette() + number;
                    result = userService.updateRoulette(targetUserName, newCount, operator);
                } else if (Arrays.asList("抽卡", "抽卡次数").contains(operation)) {
                    int newCount = setCount ? number : targetUser.getRollCount() + number;
                    result = userService.updateRollCount(targetUserName, newCount, operator);
                }
            } catch (OperationException oe) {
                result = "操作失败：" + oe.getMessage();
            }
            if (!StringUtils.isEmpty(result)) {
                broadcastFacade.sendMsgAsync(buildResponse(result, o, true));
            }
        }
    }
}
