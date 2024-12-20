package com.weird.handler.chatimpl;

import com.alibaba.fastjson.JSONObject;
import com.weird.config.AutoConfig;
import com.weird.facade.BroadcastFacade;
import com.weird.handler.ChatHandler;
import com.weird.model.bo.ExchangeRequestBO;
import com.weird.model.dto.CardListDTO;
import com.weird.model.dto.CardSwapDTO;
import com.weird.model.dto.UserDataDTO;
import com.weird.model.enums.ExchangeRequestEnum;
import com.weird.model.param.SearchCardParam;
import com.weird.service.CardPreviewService;
import com.weird.service.CardService;
import com.weird.service.UserService;
import com.weird.utils.OperationException;
import com.weird.utils.PackageUtil;
import com.weird.utils.ResponseException;
import com.weird.utils.StringExtendUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static com.weird.utils.BroadcastUtil.*;

/**
 * 自助交换
 *
 * @author Nidhogg
 * @date 2021.10.28
 */
@Component
@Slf4j
public class ChatExchangeHandler implements ChatHandler {
    @Autowired
    BroadcastFacade broadcastFacade;

    @Autowired
    UserService userService;

    @Autowired
    CardService cardService;

    @Autowired
    CardPreviewService cardPreviewService;

    static Map<String, ExchangeRequestBO> exchangeMap = new HashMap<>();

    final static List<String> SPLIT_STR_LIST = Arrays.asList(">交易", ">交换");

    final static List<String> AGREE_STR_LIST = Arrays.asList("同意", "接受", "接收", "A", "a");

    final static List<String> REJECT_STR_LIST = Arrays.asList("不同意", "取消", "拒绝", "R", "r");

    final static String HELP_STR = "查看帮助请使用以下指令：\n>帮助 交换";

    final static long TIME_GAP = 1000 * 60 * 90;

    @Override
    public void handle(JSONObject o) throws Exception {
        String message = o.getString(MESSAGE);
        String userQQ = o.getString(QQ);
        for (String splitStr : SPLIT_STR_LIST) {
            if (message.startsWith(splitStr)) {
                UserDataDTO userData = userService.getUserByQQ(userQQ);
                if (userData == null) {
                    broadcastFacade.sendMsgAsync(buildResponse(NOT_BIND_WARNING, o));
                    return;
                }

                List<String> argList = appendNameWithAt(StringExtendUtil.split(message.substring(splitStr.length()).trim(), " "));
                if (CollectionUtils.isEmpty(argList) || (argList.size() == 1 && "列表".equals(argList.get(0)))) {
                    // 查阅
                    printList(o);
                } else if (argList.size() == 2) {
                    // 操作
                    operate(userData, argList, o);
                } else if (argList.size() == 3) {
                    // 发起
                    request(userData, argList, o);
                } else {
                    broadcastFacade.sendMsgAsync(buildResponse(HELP_STR, o));
                }
                break;
            }
        }
    }

    /**
     * 更新交换请求列表，移除过期请求
     */
    private synchronized void refreshExchangeList() {
        Iterator<Map.Entry<String, ExchangeRequestBO>> iterator = exchangeMap.entrySet().iterator();
        List<ExchangeRequestBO> removedRequestList = new LinkedList<>();
        while (iterator.hasNext()) {
            Map.Entry<String, ExchangeRequestBO> data = iterator.next();
            long requestTime = data.getValue().getRequestTime();
            if (System.currentTimeMillis() - requestTime > TIME_GAP) {
                removedRequestList.add(data.getValue());
                iterator.remove();
            }
        }
        if (!CollectionUtils.isEmpty(removedRequestList)) {
            StringBuilder sb = new StringBuilder();
            for (ExchangeRequestBO requestBO : removedRequestList) {
                sb.append("你的以下交换请求已超时：\n").append(requestBO.print());
                broadcastFacade.sendMsgAsync(buildResponse(sb.toString(), requestBO.getRequest(), true));
                sb.setLength(0);
            }
        }
    }

    /**
     * 打印交换请求
     */
    private void printList(JSONObject o) {
        refreshExchangeList();
        StringBuilder sb = new StringBuilder();
        if (exchangeMap.size() == 0) {
            sb.append("最近的交换请求：");
            sb.append("\n无");
        } else {
            List<ExchangeRequestBO> waitingTargetList = new LinkedList<>();
            List<ExchangeRequestBO> waitingAdminList = new LinkedList<>();
            for (Map.Entry<String, ExchangeRequestBO> entry : exchangeMap.entrySet()) {
                final ExchangeRequestBO requestBO = entry.getValue();
                switch (requestBO.getStatus()) {
                    case WAITING_TARGET:
                        waitingTargetList.add(requestBO);
                        break;
                    case WAITING_ADMIN:
                        waitingAdminList.add(requestBO);
                        break;
                    default:
                        break;
                }
            }
            sb.append("等待对方同意的交换请求：");
            printSubList(waitingTargetList, sb);
            sb.append("\n等待管理员处理的交换：");
            printSubList(waitingAdminList, sb);
            sb.append("\n帮助信息请见：\n>帮助 交换");
        }
        broadcastFacade.sendMsgAsync(buildResponse(sb.toString(), o));
    }

    private void printSubList(List<ExchangeRequestBO> requestList, StringBuilder sb) {
        if (CollectionUtils.isEmpty(requestList)) {
            sb.append("\n无");
        } else {
            for (ExchangeRequestBO requestBO : requestList) {
                sb.append("\n").append(requestBO.print());
            }
        }
    }

    /**
     * 发起交换请求
     *
     * @param userData 当前用户
     * @param argList  参数列表
     * @param o        处理交换的消息
     */
    private void request(UserDataDTO userData, List<String> argList, JSONObject o) throws ResponseException {
        String currentUserName = userData.getUserName();
        if (userService.adminCheck(currentUserName)) {
            throw new ResponseException("管理员不可进行交换！");
        }
        refreshExchangeList();
        for (ExchangeRequestBO data : exchangeMap.values()) {
            if (data.getUserName().equals(currentUserName)) {
                throw new ResponseException("一个用户只能同时发起一次交换！");
            }
        }

        String selfCardNameArg = argList.get(0);
        String targetUserArg = argList.get(1);
        String targetCardNameArg = argList.get(2);
        Matcher matcher = AT_PATTERN.matcher(targetUserArg);
        if (matcher.matches()) {
            targetUserArg = matcher.group(1);
        }
        UserDataDTO targetUser = userService.getUserByQQ(targetUserArg);
        if (targetUser == null) {
            throw new ResponseException("对方尚未绑定帐号！");
        }
        String targetUserName = targetUser.getUserName();
        if (userService.adminCheck(targetUserName)) {
            throw new ResponseException("管理员不可进行交换！");
        } else if (currentUserName.equals(targetUserName)) {
            throw new ResponseException("不能左手倒右手！");
        }

        String selfCardName = fetchExchangeTarget(currentUserName, selfCardNameArg).getCardName();
        String targetCardName = fetchExchangeTarget(targetUserName, targetCardNameArg).getCardName();
        if (selfCardName.equals(targetCardName)) {
            throw new ResponseException("双方交换的卡片相同！");
        }
        if (PackageUtil.onlyByRoll(selfCardName) || PackageUtil.onlyByRoll(targetCardName)) {
            throw new ResponseException("不能交换特殊卡片！");
        }

        try {
            String exchangeCondition = AutoConfig.fetchExchangeCard();
            if (!StringUtils.isEmpty(exchangeCondition)) {
                int exchange = userService.getUserOwnCardCount(currentUserName, exchangeCondition);
                if (exchange == 0) {
                    throw new ResponseException("你的[%s]不足！", exchangeCondition);
                }
            }
        } catch (OperationException oe) {
            throw new ResponseException(oe.getMessage());
        }

        ExchangeRequestBO requestBO = new ExchangeRequestBO(currentUserName, targetUserName, selfCardName, targetCardName, o);
        String hash = DigestUtils.md5DigestAsHex(requestBO.toString().getBytes()).substring(0, 4);
        requestBO.setHash(hash);
        exchangeMap.put(hash, requestBO);
        broadcastFacade.sendMsgAsync(buildResponse(String.format("已成功申请用[%s]交换%s的[%s]，对方可用以下指令同意：\n>交换 同意 %s",
                selfCardName, targetUserName, targetCardName, hash), o));
    }

    /**
     * 对交换请求进行操作
     *
     * @param userData 当前用户
     * @param argList  参数列表
     * @param o        处理交换的消息
     */
    private void operate(UserDataDTO userData, List<String> argList, JSONObject o) throws ResponseException {
        String operation = argList.get(0);
        String hash = argList.get(1);
        final String currentUserName = userData.getUserName();

        ExchangeRequestEnum operationEnum = null;
        if (AGREE_STR_LIST.contains(operation)) {
            operationEnum = ExchangeRequestEnum.AGREE;
        } else if (REJECT_STR_LIST.contains(operation)) {
            operationEnum = ExchangeRequestEnum.REJECT;
        } else {
            throw new ResponseException("命令格式错误，" + HELP_STR);
        }

        ExchangeRequestBO requestBO = exchangeMap.getOrDefault(hash, null);
        if (requestBO == null) {
            throw new ResponseException("交换请求[%s]不存在！", hash);
        }
        boolean isAdmin = userService.adminCheck(currentUserName);

        // 同意
        if (operationEnum == ExchangeRequestEnum.AGREE) {
            if (requestBO.getStatus() == ExchangeRequestEnum.WAITING_ADMIN) {
                // 管理员同意交换
                if (!isAdmin) {
                    throw new ResponseException("非管理员无法同意！");
                }
                solveExchange(requestBO, hash, currentUserName, o);

            } else if (requestBO.getStatus() == ExchangeRequestEnum.WAITING_TARGET) {
                // 对方同意交换
                if (requestBO.getUserName().equals(currentUserName)) {
                    throw new ResponseException("请由对方同意！");
                } else if (!isAdmin && !requestBO.getTargetName().equals(currentUserName)) {
                    throw new ResponseException("你无权同意！");
                }
                int cutoffCount = -1;
                String exchangeCondition = AutoConfig.fetchExchangeCard();
                try {
                    if (!StringUtils.isEmpty(exchangeCondition)) {
                        cutoffCount = userService.getUserOwnCardCount(requestBO.getUserName(), exchangeCondition);
                        if (cutoffCount == 0) {
                            throw new ResponseException("[%s]的[%s]不足，无法处理交换！", requestBO.getUserName(), exchangeCondition);
                        }
                        cutoffCount -= 1;
                    }
                } catch (OperationException oe) {
                    throw new ResponseException(oe.getMessage());
                }
                if (cutoffCount >= 0) {
                    solveExchange(requestBO, hash, currentUserName, o);
                    try {
                        cardService.updateCardCount(requestBO.getUserName(), exchangeCondition, cutoffCount, requestBO.getUserName());
                    } catch (OperationException e) {
                        broadcastFacade.sendMsgAsync(buildResponse(String.format("[%s]的[%s]扣除失败，请检查。", requestBO.getUserName(), exchangeCondition), o, true));
                    }
                } else {
                    requestBO.setStatus(ExchangeRequestEnum.WAITING_ADMIN);
                    broadcastFacade.sendMsgAsync(buildResponse("你已同意交换，请等待管理员进行交换：\n>交换 同意 " + hash, o, true));
                }
            }
            // 拒绝
        } else {
            if (isAdmin || requestBO.getUserName().equals(currentUserName) || requestBO.getTargetName().equals(currentUserName)) {
                exchangeMap.remove(hash);
                broadcastFacade.sendMsgAsync(buildResponse("交换[" + hash + "]已取消！", o));
            } else {
                throw new ResponseException("你无权拒绝！");
            }
        }
    }

    private void solveExchange(ExchangeRequestBO requestBO, String hash, String currentUserName, JSONObject o) throws ResponseException {
        try {
            exchangeMap.remove(hash);
            CardSwapDTO swapParam = new CardSwapDTO();
            swapParam.setUserA(requestBO.getUserName());
            swapParam.setUserB(requestBO.getTargetName());
            swapParam.setCardA(requestBO.getSelfCardName());
            swapParam.setCardB(requestBO.getTargetCardName());
            swapParam.setName(currentUserName);
            String response = userService.swapCard(swapParam, currentUserName);

            String exchangeCardName = AutoConfig.fetchExchangeCard();
            if (!StringUtils.isEmpty(exchangeCardName)) {
                int userExchangeCount = userService.getUserOwnCardCount(requestBO.getUserName(), exchangeCardName);
                response += String.format("\n%s的[%s]: %d->%d", requestBO.getUserName(), exchangeCardName, userExchangeCount + 1, userExchangeCount);
            }

            broadcastFacade.sendMsgAsync(buildResponse(response, o, true));
        } catch (OperationException e) {
            throw new ResponseException(e.getMessage());
        }
    }

    /**
     * 获取交换对象
     *
     * @param userName 发起用户
     * @param cardName 卡片名
     * @return 重抽卡片对象
     */
    private CardListDTO fetchExchangeTarget(String userName, String cardName) throws ResponseException {
        SearchCardParam param = new SearchCardParam();
        param.setCardName(cardName);
        param.setName(userName);
        param.setRareList(Arrays.asList("SR", "UR", "HR"));
        List<String> alterList = cardPreviewService.blurSearch(param.getCardName());
        List<CardListDTO> dtoList = cardService.selectListUser(param, alterList);
        dtoList = dtoList.stream().filter(c -> c.getCount() > 0).collect(Collectors.toList());

        // 查找目标卡片
        CardListDTO targetCard = dtoList.stream().filter(c -> c.getCardName().equals(cardName)).findFirst().orElse(null);
        if (targetCard == null) {
            if (dtoList.isEmpty()) {
                throw new ResponseException("找不到卡片：[%s]！", cardName);
            } else if (dtoList.size() > 1) {
                throw new ResponseException("[%s]找到多于一张符合条件的卡片，请修改条件重试！", cardName);
            } else {
                targetCard = dtoList.get(0);
            }
        }
        if (PackageUtil.canNotRoll(targetCard.getPackageName())) {
            throw new ResponseException("该卡片不可交换：[%s]！", cardName);
        }

        return targetCard;
    }

    /**
     * 将有at的内容以at为分隔符进行前后合并，以兼容空格卡名
     *
     * @param targetList
     * @return
     */
    private List<String> appendNameWithAt(List<String> targetList) {
        if (CollectionUtils.isEmpty(targetList)) {
            return Collections.emptyList();
        }
        List<String> stack = new LinkedList<>();
        List<String> resultList = new LinkedList<>();
        boolean at = false;
        for (String str : targetList) {
            Matcher matcher = AT_PATTERN.matcher(str);
            if (matcher.matches()) {
                at = true;
                resultList.add(String.join(" ", stack));
                resultList.add(str);
                stack.clear();
            } else {
                stack.add(str);
            }
        }
        if (at) {
            resultList.add(String.join(" ", stack));
        } else {
            resultList.addAll(stack);
        }

        return resultList;
    }
}