package com.weird.handler.chatimpl;

import com.alibaba.fastjson.JSONObject;
import com.weird.config.AutoConfig;
import com.weird.config.DuelConfig;
import com.weird.facade.BroadcastFacade;
import com.weird.handler.ChatHandler;
import com.weird.model.dto.CardListDTO;
import com.weird.model.dto.UserDataDTO;
import com.weird.model.param.SearchCardParam;
import com.weird.service.CardPreviewService;
import com.weird.service.CardService;
import com.weird.service.UserService;
import com.weird.utils.OperationException;
import com.weird.utils.ResponseException;
import com.weird.utils.StringExtendUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.smartcardio.CardNotPresentException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    @Autowired
    CardPreviewService cardPreviewService;

    @Autowired
    CardService cardService;

    final static String SPLIT_STR = ">sudo ";

    static public Pattern ADD_PATTERN = Pattern.compile("\\+(\\d+)");

    static public Pattern SUB_PATTERN = Pattern.compile("-(\\d+)");

    static public Pattern EQ_PATTERN = Pattern.compile("=(\\d+)");

    static public List<String> DUST_OPERATION_LIST = Arrays.asList("尘", "dust", "Dust", "DUST");
    static public List<String> COIN_OPERATION_LIST = Arrays.asList("硬币", "coin", "Coin", "COIN");
    static public List<String> AWARD_OPERATION_LIST = Arrays.asList("月见黑", "黑", "award", "noaward", "nonaward");
    static public List<String> DP_OPERATION_LIST = Arrays.asList("DP", "dp", "Dp");
    static public List<String> ROULETTE_OPERATION_LIST = Arrays.asList("转盘", "转盘次数");
    static public List<String> ROLL_COUNT_OPERATION_LIST = Arrays.asList("抽卡", "抽卡次数");
    static public List<String> GIFT_OPERATION_LIST = Arrays.asList("交换", "交换券", "py", "PY");

    @Override
    public void handle(JSONObject o) throws ResponseException {
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
                if (DUST_OPERATION_LIST.contains(operation)) {
                    int newCount = setCount ? number : targetUser.getDustCount() + number;
                    result = userService.updateDust(targetUserName, newCount, operator);
                } else if (COIN_OPERATION_LIST.contains(operation)) {
                    int newCount = setCount ? number : targetUser.getCoin() + number;
                    result = userService.updateCoin(targetUserName, newCount, operator);
                } else if (AWARD_OPERATION_LIST.contains(operation)) {
                    int newCount = setCount ? number : targetUser.getNonawardCount() + number;
                    result = userService.updateAward(targetUserName, newCount, operator);
                } else if (DP_OPERATION_LIST.contains(operation)) {
                    int newCount = setCount ? number : targetUser.getDuelPoint() + number;
                    result = userService.updateDuelPoint(targetUserName, newCount, operator);
                } else if (ROULETTE_OPERATION_LIST.contains(operation)) {
                    int newCount = setCount ? number : targetUser.getRoulette() + number;
                    result = userService.updateRoulette(targetUserName, newCount, operator);
                } else if (ROLL_COUNT_OPERATION_LIST.contains(operation)) {
                    int newCount = setCount ? number : targetUser.getRollCount() + number;
                    result = userService.updateRollCount(targetUserName, newCount, operator);
                } else {
                    if (GIFT_OPERATION_LIST.contains(operation)) {
                        String exchangeCardName = AutoConfig.fetchExchangeCard();
                        if (!StringUtils.isEmpty(exchangeCardName)) {
                            operation = exchangeCardName;
                        }
                    }
                    String cardName = fetchCard(operation);
                    int currentCount = userService.getUserOwnCardCount(targetUserName, cardName);
                    int newCount = setCount ? number : currentCount + number;
                    result = cardService.updateCardCount(targetUserName, cardName, newCount, operator);
                }
            } catch (OperationException oe) {
                result = "操作失败：" + oe.getMessage();
            }
            if (!StringUtils.isEmpty(result)) {
                broadcastFacade.sendMsgAsync(buildResponse(result, o, true));
            }
        }
    }

    /**
     * 获取需要修改的卡片
     *
     * @param cardName 卡片名
     * @return 重抽卡片对象
     */
    private String fetchCard(String cardName) throws ResponseException {
        SearchCardParam param = new SearchCardParam();
        param.setCardName(cardName);
        List<String> alterList = cardPreviewService.blurSearch(param.getCardName());
        List<CardListDTO> dtoList = cardService.selectListAdmin(param, alterList);
        dtoList = dtoList.stream().filter(c -> c.getCount() > 0).collect(Collectors.toList());

        // 查找目标卡片
        CardListDTO targetCard = dtoList.stream().filter(c -> c.getCardName().equals(cardName)).findFirst().orElse(null);
        if (targetCard == null) {
            if (dtoList.isEmpty()) {
                throw new ResponseException("找不到该卡片！");
            } else if (dtoList.size() > 1) {
                throw new ResponseException("找到多于一张符合条件的卡片，请修改条件重试！");
            } else {
                targetCard = dtoList.get(0);
            }
        }

        return targetCard.getCardName();
    }
}
