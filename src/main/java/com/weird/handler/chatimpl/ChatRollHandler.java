package com.weird.handler.chatimpl;

import com.alibaba.fastjson.JSONObject;
import com.weird.config.AutoConfig;
import com.weird.config.WeirdConfig;
import com.weird.facade.BroadcastFacade;
import com.weird.handler.ChatHandler;
import com.weird.model.CardPreviewModel;
import com.weird.model.PackageInfoModel;
import com.weird.model.bo.RollPackageBO;
import com.weird.model.bo.RollRequestBO;
import com.weird.model.dto.CardListDTO;
import com.weird.model.dto.CardOwnListDTO;
import com.weird.model.dto.UserDataDTO;
import com.weird.model.enums.RollRequestTypeEnum;
import com.weird.model.param.ReplaceCardParam;
import com.weird.model.param.SearchCardParam;
import com.weird.service.*;
import com.weird.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.weird.utils.BroadcastUtil.*;

/**
 * 自助抽卡
 *
 * @author Nidhogg
 * @date 2021.10.15
 */
@Component
@Slf4j
public class ChatRollHandler implements ChatHandler {
    @Autowired
    BroadcastFacade broadcastFacade;

    @Autowired
    UserService userService;

    @Autowired
    PackageService packageService;

    @Autowired
    CardService cardService;

    @Autowired
    RollService rollService;

    @Autowired
    CardPreviewService cardPreviewService;

    static Map<String, RollRequestBO> rollMap = new HashMap<>();

    final static String SPLIT_STR = ">抽卡";

    final static long TIME_GAP = 1000 * 60 * 90;

    final static String ARG_LIST = "列表";

    final static String ARG_CANCEL = "取消";

    final static String ARG_RARE_TO_STOP = "闪停";

    final static String ARG_SHOW_ALL = "全";

    final static List<String> METHOD_ROLL_ALL_LIST = Arrays.asList("all", "ALL", "全部", "全");

    final static List<String> ARG_REROLL_LIST = Arrays.asList("重抽", "reroll", "REROLL", "Reroll", "ReRoll", "万宝槌", "万宝锤", "锤");

    final static List<String> ARG_LEGEND_LIST = Arrays.asList("抽传说", "传说");

    final static List<String> ARG_PREFIX_LIST = Arrays.asList("重抽", "reroll", "REROLL", "Reroll", "ReRoll", "万宝槌", "万宝锤", "锤", "抽传说", "传说");

    @Override
    public void handle(JSONObject o) throws Exception {
        String message = o.getString(MESSAGE);
        String userQQ = o.getString(QQ);
        // 不处理私聊抽卡请求
        if (!"group".equals(o.getString(MESSAGE_TYPE))) {
            return;
        }
        List<String> argList = null;
        UserDataDTO userData = null;
        if (message.startsWith(SPLIT_STR)) {
            userData = userService.getUserByQQ(userQQ);
            if (userData == null) {
                broadcastFacade.sendMsgAsync(buildResponse(NOT_BIND_WARNING, o));
                return;
            }
            String args = message.substring(SPLIT_STR.length()).trim();
            argList = StringExtendUtil.split(args, " ");
        } else {
            for (String prefix : ARG_PREFIX_LIST) {
                String splitStr = ">" + prefix;
                if (message.startsWith(splitStr)) {
                    userData = userService.getUserByQQ(userQQ);
                    if (userData == null) {
                        broadcastFacade.sendMsgAsync(buildResponse(NOT_BIND_WARNING, o));
                        return;
                    }

                    String args = message.substring(splitStr.length()).trim();
                    argList = new LinkedList<>();
                    argList.add(prefix);
                    argList.addAll(StringExtendUtil.split(args, " "));
                    break;
                }
            }
        }

        if (argList == null) {
            return;
        }
        if (CollectionUtils.isEmpty(argList)) {
            // >抽卡
            printRollList(o);
        } else if (argList.get(0).equals("帮助")) {
            broadcastFacade.sendMsgAsync(buildResponse("查看帮助请使用以下指令：\n>帮助 抽卡", o));
        } else if (argList.size() == 1) {
            if (ARG_LIST.equals(argList.get(0))) {
                // >抽卡 列表
                printRollList(o);
            } else {
                // 管理员同意抽卡
                refreshRollRequest();
                responseRoll(userData, argList, o);
            }
        } else {
            String firstArg = argList.get(0).trim();
            if (ARG_CANCEL.equals(firstArg)) {
                // 取消抽卡请求
                rollBackRoll(userData, argList, o);
            } else if (ARG_REROLL_LIST.contains(firstArg)) {
                // 重抽
                requestReRoll(userData, argList, o);
            } else {
                // 发起抽卡请求
                requestDraw(userData, argList, o);
            }
        }
    }

    /**
     * 玩家请求抽卡操作
     *
     * @param userData 用户数据
     * @param argList  参数列表
     * @param o        请求消息
     * @throws Exception
     */
    private void requestDraw(UserDataDTO userData, List<String> argList, JSONObject o) throws Exception {
        String userName = userData.getUserName();
        if (userService.adminCheck(userName)) {
            throw new ResponseException("管理员不可发起抽卡请求！");
        }

        // 解析参数
        int rollCount;
        String packageArg = argList.get(0).trim();
        String countArg = argList.get(1).trim();
        List<String> otherArgList = argList.subList(2, argList.size());
        try {
            rollCount = Integer.parseInt(countArg);
            if (rollCount <= 0) {
                throw new OperationException("请输入合法的数量！");
            }
        } catch (NumberFormatException | OperationException ne) {
            throw new ResponseException("请输入合法的数量！");
        }

        // 判断抽卡合法性
        refreshRollRequest();
        for (RollRequestBO data : rollMap.values()) {
            if (data.getUserName().equals(userName) && data.getType() == RollRequestTypeEnum.NORMAL) {
                throw new ResponseException("一个用户只能同时申请一次抽卡！");
            }
        }
        if (rollCount > 50) {
            throw new ResponseException("一次申请只能进行最多50次抽卡！");
        }

        // 获取卡包
        PackageInfoModel pack = fetchPackageInfo(packageArg);

        RollRequestBO requestBO = new RollRequestBO(userName, pack, rollCount, o);
        requestBO.setOriginDp(userData.getDuelPoint());
        String remark = "";
        if (otherArgList.contains(ARG_RARE_TO_STOP)) {
            requestBO.setRareToStop(true);
            remark = "(闪停)";
        }
        if (otherArgList.contains(ARG_SHOW_ALL)) {
            requestBO.setShowAll(true);
        }
        String hash = DigestUtils.md5DigestAsHex(requestBO.toString().getBytes()).substring(0, 4);

        if (AutoConfig.fetchDp()) {
            int duelPoint = userData.getDuelPoint();
            int needDp = rollCount * 5;
            if (duelPoint < needDp) {
                broadcastFacade.sendMsgAsync(buildResponse(String.format("需要%dDP方可进行抽卡，你当前只有%dDP！", needDp, duelPoint), o));
                return;
            }
            rollMap.put(hash, requestBO);
            handleDrawRequest(hash, userData, o);
        } else {
            rollMap.put(hash, requestBO);
            broadcastFacade.sendMsgAsync(buildResponse(String.format("已成功申请抽取%d包[%s]%s，管理员可用以下指令进行同意：\n>抽卡 %s",
                    rollCount, pack.getPackageName(), remark, hash), o));
        }
    }

    /**
     * 玩家请求重抽
     *
     * @param userData 用户数据
     * @param argList  参数列表
     * @param o        请求消息
     */
    private void requestReRoll(UserDataDTO userData, List<String> argList, JSONObject o) throws Exception {
        String userName = userData.getUserName();
        if (userService.adminCheck(userName)) {
            throw new ResponseException("管理员不可发起重抽请求！");
        }

        String cardName = argList.get(1);
        // 查找目标卡片
        CardListDTO targetCard = fetchReRollTarget(userData.getUserName(), cardName, "", true);
        String packageName = targetCard.getPackageName();
        if (StringUtils.isEmpty(packageName)) {
            throw new ResponseException("[%s]的卡包信息有误，请联系管理员！", targetCard.getCardName());
        }

        // 获取卡包
        PackageInfoModel pack = fetchPackageInfo(targetCard.getPackageName());

        // 判断抽卡合法性
        refreshRollRequest();
        for (RollRequestBO data : rollMap.values()) {
            if (data.getUserName().equals(userName) && data.getType() == RollRequestTypeEnum.REROLL) {
                throw new ResponseException("一个用户只能同时申请一次重抽！");
            }
        }

        if (PackageUtil.onlyByRoll(targetCard.getCardName())) {
            throw new ResponseException("不能重抽特殊卡片！");
        }

        String reRollCondition = AutoConfig.fetchReRollCard();
        boolean solveDirect = false;
        if (!StringUtils.isEmpty(reRollCondition)) {
            int reRollCount = userService.getUserOwnCardCount(userName, reRollCondition);
            if (reRollCount == 0) {
                throw new ResponseException("你的[%s]不足！", reRollCondition);
            }
            solveDirect = true;
        }

        // 发起重抽
        RollRequestBO requestBO = new RollRequestBO(userName, pack, 0, o, RollRequestTypeEnum.REROLL);
        requestBO.setReRollCardName(targetCard.getCardName());
        String hash = DigestUtils.md5DigestAsHex(requestBO.toString().getBytes()).substring(0, 4);
        rollMap.put(hash, requestBO);
        if (solveDirect) {
            handleReRollRequest(hash, userData, o);
        } else {
            broadcastFacade.sendMsgAsync(buildResponse(String.format("已成功申请重抽[%s]的[%s]，管理员可用以下指令进行同意：\n>抽卡 %s",
                    pack.getPackageName(), targetCard.getCardName(), hash), o));
        }
    }

    /**
     * 玩家请求抽传说
     *
     * @param userData 用户数据
     * @param o        请求消息
     */
    private void requestLegend(UserDataDTO userData, JSONObject o) throws ResponseException {
        String userName = userData.getUserName();
        if (userService.adminCheck(userName)) {
            throw new ResponseException("管理员不可发起抽传说请求！");
        }

        PackageInfoModel legendPackage = fetchLegendPackageInfo();
        CardListDTO targetCard = fetchReRollTarget(userData.getUserName(), "", legendPackage.getPackageName(), false);
        String currentLegendName = "";
        if (targetCard != null) {
            currentLegendName = targetCard.getCardName();
        }

        // 判断抽卡合法性
        refreshRollRequest();
        for (RollRequestBO data : rollMap.values()) {
            if (data.getUserName().equals(userName) && (data.getType() == RollRequestTypeEnum.LEGEND || data.getType() == RollRequestTypeEnum.LEGEND_CONFIRM)) {
                throw new ResponseException("一个用户只能同时申请一次抽传说！");
            }
        }

        // 发起抽传说
        RollRequestBO requestBO = new RollRequestBO(userName, null, 0, o, RollRequestTypeEnum.LEGEND);
        requestBO.setOriginDp(userData.getDuelPoint());
        requestBO.setReRollCardName(currentLegendName);
        String hash = DigestUtils.md5DigestAsHex(requestBO.toString().getBytes()).substring(0, 4);

        if (AutoConfig.fetchDp()) {
            int duelPoint = userData.getDuelPoint();
            int needDp = 100;
            if (duelPoint < needDp) {
                broadcastFacade.sendMsgAsync(buildResponse(String.format("需要%dDP方可抽传说，你当前只有%dDP！", needDp, duelPoint), o));
                return;
            }
            rollMap.put(hash, requestBO);
            handleLegendRequest(hash, userData, o);
        } else {
            rollMap.put(hash, requestBO);
            if (StringUtils.isEmpty(currentLegendName)) {
                broadcastFacade.sendMsgAsync(buildResponse(String.format("已成功申请抽传说卡，管理员可用以下指令进行同意：\n>抽卡 %s", hash), o));
            } else {
                broadcastFacade.sendMsgAsync(buildResponse(String.format("已成功申请重抽传说卡(当前为[%s])，管理员可用以下指令进行同意：\n>抽卡 %s",
                        currentLegendName, hash), o));
            }
        }
    }

    /**
     * 管理员处理信息
     *
     * @param userData 用户数据
     * @param argList  参数列表
     * @param o        请求消息
     */
    private synchronized void responseRoll(UserDataDTO userData, List<String> argList, JSONObject o) throws ResponseException {
        String arg = argList.get(0);
        if (ARG_LEGEND_LIST.contains(arg)) {
            // 抽传说卡
            requestLegend(userData, o);
            return;
        }
        RollRequestBO request = rollMap.getOrDefault(arg, null);
        if (request != null && request.getType() == RollRequestTypeEnum.LEGEND_CONFIRM) {
            if (!userService.adminCheck(userData.getUserName()) && !request.getUserName().equals(userData.getUserName())) {
                throw new ResponseException("你无权执行该抽卡请求！");
            }
            handleLegendConfirm(arg, userData, o);
            return;
        }

        if (!userService.adminCheck(userData.getUserName())) {
            broadcastFacade.sendMsgAsync(buildResponse("请输入正确的格式：\n>抽卡 卡包名 数量", o));
            return;
        }

        if (METHOD_ROLL_ALL_LIST.contains(arg)) {
            List<String> hashList = new LinkedList<>(rollMap.keySet());
            int successCount = 0;
            for (String hash : hashList) {
                RollRequestBO currentRequest = rollMap.getOrDefault(hash, null);
                if (currentRequest.getType() != RollRequestTypeEnum.NORMAL) {
                    continue;
                }
                try {
                    if (handleDrawRequest(hash, userData, o)) {
                        successCount++;
                    }
                } catch (ResponseException responseException) {
                    broadcastFacade.sendMsgAsync(buildResponse(responseException.getMessage(), o));
                }
            }
            broadcastFacade.sendMsgAsync(buildResponse(String.format("成功处理%d条抽卡请求！", successCount), o, true));
        } else {
            if (request == null) {
                throw new ResponseException("请求[%s]不存在！", arg);
            }
            switch (request.getType()) {
                case NORMAL:
                    handleDrawRequest(arg, userData, o);
                    break;
                case REROLL:
                    handleReRollRequest(arg, userData, o);
                    break;
                case LEGEND:
                    handleLegendRequest(arg, userData, o);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 处理抽卡
     *
     * @param hash     散列key
     * @param operator 操作人
     * @param response 处理抽卡的消息
     * @return
     */
    private boolean handleDrawRequest(String hash, UserDataDTO operator, JSONObject response) throws ResponseException {
        RollRequestBO request = rollMap.getOrDefault(hash, null);
        Random rd = new Random();
        if (request == null) {
            throw new ResponseException("抽卡请求[%s]不存在！", hash);
        }
        if (request.getType() != RollRequestTypeEnum.NORMAL) {
            throw new ResponseException("无法处理非抽卡请求[%s]！", hash);
        }
        final String requestUserName = request.getUserName();

        // 获取抽卡信息
        int remainRollCount = request.getRollCount();
        int remainDoubleRareCount = userService.getDoubleRareCount(requestUserName);
        int originDoubleRareCount = remainDoubleRareCount;
        rollMap.remove(hash);

        // 查询卡包卡片内容
        RollPackageBO packageInfo = fetchPackageCardList(request.getPackageInfo().getPackageName());
        if (packageInfo.getNormalList().size() < 2
                || packageInfo.getRareList().size() <= 0
                || packageInfo.getSrList().size() <= 0
                || packageInfo.getUrList().size() <= 0
                || packageInfo.getHrList().size() <= 0) {
            throw new ResponseException("[%s]的卡包配置有误，请联系管理员", request.getPackageInfo().getPackageName());
        }

        // 合并消息，默认展示全部
        request.setShowAll(true);

        // 记录抽卡结果
        List<List<CardListDTO>> cardResultAllList = new LinkedList<>();
        int totalRareCount = 0;
        int resultIndex = 1;
        List<String> resultResponseList = new ArrayList<>();
        resultResponseList.add(requestUserName + "的抽卡结果：");

        while (remainRollCount-- > 0) {
            // 生成抽卡内容
            List<CardListDTO> cardResultList = new LinkedList<>(packageInfo.getRandomResultFromNormal());
            int rareRate = PackageUtil.NORMAL_RARE_RATE;
            if (remainDoubleRareCount > 0) {
                rareRate = PackageUtil.DOUBLE_RARE_RATE;
                remainDoubleRareCount--;
            }

            boolean currentRare = rd.nextInt(100) < rareRate;
            if (currentRare) {
                // 0-3 闪卡
                List<CardListDTO> awardCardList;
                // HR-UR-SR按照1-2-5比例出现
                int awardTypeRand = rd.nextInt(8);
                switch (awardTypeRand) {
                    case 0:
                        awardCardList = packageInfo.getHrList();
                        break;
                    case 1:
                    case 2:
                        awardCardList = packageInfo.getUrList();
                        break;
                    default:
                        awardCardList = packageInfo.getSrList();
                    break;
                }
                cardResultList.add(awardCardList.get(rd.nextInt(awardCardList.size())));
                totalRareCount++;
            } else {
                // 其他 R
                cardResultList.add(packageInfo.getRareList().get(rd.nextInt(packageInfo.getRareList().size())));
            }
            cardResultAllList.add(cardResultList);

            // 生成消息
            if (currentRare || request.isShowAll()) {
                String singleResult = String.valueOf(resultIndex);
                if (rareRate == PackageUtil.DOUBLE_RARE_RATE) {
                    singleResult += "[百八]";
                }
                singleResult += "：";
                singleResult += cardResultList.stream().map(PackageUtil::printCard).collect(Collectors.joining("、"));
                resultResponseList.add(singleResult);
            }
            if (currentRare && request.isRareToStop()) {
                break;
            }
            resultIndex++;
        }

        // 发送抽卡结果
        StringBuilder exceptBuilder = new StringBuilder();
        for (List<CardListDTO> resultList : cardResultAllList) {
            try {
                rollService.roll(resultList.stream().map(CardListDTO::getCardName).collect(Collectors.toList()), requestUserName, operator.getUserName());
            } catch (Exception e) {
                exceptBuilder.append("\n").append(e.getMessage());
            }
        }
        if (originDoubleRareCount > 0 && originDoubleRareCount != remainDoubleRareCount) {
            userService.updateDoubleRareCount(requestUserName, remainDoubleRareCount, operator.getUserName());
        }

        String rollResult = "";
        long totalRollCount = cardResultAllList.size();
        if (totalRareCount == 0) {
            rollResult = "真是可惜，" + totalRollCount + "包卡里一张闪都没有！";
            if (userService.getUserDailyReward(requestUserName) <= 0) {
                rollResult += "\n你今天仍然没有抽到闪卡，再接再厉！";
            }
        } else if (totalRareCount == 1 && totalRollCount > totalRareCount) {
            rollResult = totalRollCount + "包卡里出了1张闪，不错，很有精神！";
        } else if (totalRareCount > 1 || totalRollCount == totalRareCount) {
            rollResult = "难道是抽卡机出了什么问题？" + totalRollCount + "包里可以出" + totalRareCount + "张闪的吗？";
        }
        if (!request.isShowAll() && totalRollCount > totalRareCount) {
            rollResult += "\n具体的抽卡结果请前往诡异云查看。";
        }
        resultResponseList.add(rollResult);

        if (AutoConfig.fetchDp()) {
            try {
                int originDp = request.getOriginDp();
                int currentDp = userService.decDuelPoint(requestUserName, (int) (totalRollCount * 5), operator.getUserName());
                resultResponseList.add(String.format("\n%s的DP变化：%d->%d", requestUserName, originDp, currentDp));
            } catch (OperationException oe) {
                exceptBuilder.append("\n").append(oe.getMessage());
            }
        }

        if (!resultResponseList.isEmpty()) {
            if (request.isShowAll()) {
                String messageType = request.getRequest().getString("message_type");
                switch (messageType) {
                    case "group":
                        broadcastFacade.sendGroupForwardMsgAsync(buildForwardResponse(resultResponseList, request.getRequest()));
                        break;
                    case "private":
                        broadcastFacade.sendPrivateForwardMsgAsync(buildForwardResponse(resultResponseList, request.getRequest()));
                        break;
                    default:
                        broadcastFacade.sendMsgAsync(buildResponse(String.join("\n", resultResponseList), request.getRequest(), true));
                }
            } else {
                broadcastFacade.sendMsgAsync(buildResponse(String.join("\n", resultResponseList), request.getRequest(), true));
            }
        }
        String exceptString = exceptBuilder.toString();
        if (!StringUtils.isEmpty(exceptString)) {
            broadcastFacade.sendMsgAsync(buildResponse("出现以下错误，请联系管理员处理：" + exceptString, response, true));
        }

        return totalRollCount > 0;
    }

    /**
     * 处理重抽
     *
     * @param hash     散列key
     * @param operator 操作人
     * @param response 处理抽卡的消息
     * @return
     */
    private boolean handleReRollRequest(String hash, UserDataDTO operator, JSONObject response) throws ResponseException {
        RollRequestBO request = rollMap.getOrDefault(hash, null);
        Random rd = new Random();
        if (request == null) {
            throw new ResponseException("重抽请求[%s]不存在！", hash);
        }
        if (request.getType() != RollRequestTypeEnum.REROLL) {
            throw new ResponseException("无法处理非重抽请求[%s]！", hash);
        }
        final String requestUserName = request.getUserName();
        final String packageName = request.getPackageInfo().getPackageName();

        // 获取抽卡信息
        rollMap.remove(hash);
        fetchReRollTarget(requestUserName, request.getReRollCardName(), "", true);

        // 查询卡包卡片内容
        RollPackageBO packageInfo = fetchPackageCardList(packageName);
        List<CardListDTO> destCardList = packageInfo.getAwardList().stream()
                .filter(c -> !c.getCardName().equals(request.getReRollCardName()) && !PackageUtil.onlyByRoll(c.getCardName()))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(destCardList)) {
            throw new ResponseException("[%s]无法重抽，请联系管理员！", packageName);
        }

        int cutOffReRollCount = -1;
        String reRollCondition = AutoConfig.fetchReRollCard();
        try {
            if (!StringUtils.isEmpty(reRollCondition)) {
                cutOffReRollCount = userService.getUserOwnCardCount(requestUserName, reRollCondition);
                if (cutOffReRollCount == 0) {
                    throw new ResponseException("你的[%s]不足！", reRollCondition);
                }
                cutOffReRollCount -= 1;
            }
        } catch (OperationException oe) {
            throw new ResponseException(oe.getMessage());
        }

        CardListDTO destCard = destCardList.get(rd.nextInt(destCardList.size()));
        ReplaceCardParam param = new ReplaceCardParam();
        param.setName(operator.getUserName());
        param.setTargetUser(requestUserName);
        param.setOldCardName(request.getReRollCardName());
        param.setNewCardName(destCard.getCardName());
        param.setCount(1);
        try {
            userService.exchangeOwnCard(param);
        } catch (OperationException oe) {
            throw new ResponseException(oe.getMessage());
        } catch (Exception e) {
            throw new ResponseException("出现未知错误，请联系管理员");
        }

        int totalOwnCount = 0;
        int selfOwnCount = 0;
        try {
            SearchCardParam searchCountParam = new SearchCardParam();
            String cardName = destCard.getCardName();
            searchCountParam.setCardName(cardName);
            List<CardOwnListDTO> cardOwnList = cardService.selectList(searchCountParam, Collections.singletonList(cardName));
            for (CardOwnListDTO ownData : cardOwnList) {
                if (!cardName.equals(ownData.getCardName())) {
                    continue;
                }
                totalOwnCount += ownData.getCount();
                if (ownData.getUserName().equals(requestUserName)) {
                    selfOwnCount += ownData.getCount();
                }
            }
        } catch (Exception e) {
            log.error("获取卡片数量出错：", e);
        }

        String resultBuilder = String.format("[%s]对[%s]的重抽结果：\n(%d/%d)%s\n%s",
                requestUserName, request.getReRollCardName(),
                selfOwnCount, totalOwnCount, PackageUtil.printCard(destCard),
                getPreviewByName(destCard.getCardName()));

        if (cutOffReRollCount >= 0) {
            try {
                cardService.updateCardCount(requestUserName, reRollCondition, cutOffReRollCount, operator.getUserName());
                if (!StringUtils.isEmpty(reRollCondition)) {
                    int userRerollCount = userService.getUserOwnCardCount(requestUserName, reRollCondition);
                    resultBuilder += String.format("\n[%s]的[%s]: %d->%d", requestUserName, reRollCondition, userRerollCount + 1, userRerollCount);
                }
            } catch (OperationException e) {
                resultBuilder += "\n" + e.getMessage();
            }
        }

        broadcastFacade.sendMsgAsync(buildResponse(resultBuilder, request.getRequest(), true));
        return true;
    }

    /**
     * 处理抽传说
     *
     * @param hash     散列key
     * @param operator 操作人
     * @param response 处理抽卡的消息
     * @return
     */
    private boolean handleLegendRequest(String hash, UserDataDTO operator, JSONObject response) throws ResponseException {
        RollRequestBO request = rollMap.getOrDefault(hash, null);
        Random rd = new Random();
        if (request == null) {
            throw new ResponseException("抽传说请求[%s]不存在！", hash);
        }
        if (request.getType() != RollRequestTypeEnum.LEGEND) {
            throw new ResponseException("无法处理非抽传说请求[%s]！", hash);
        }
        final String requestUserName = request.getUserName();

        // 获取抽卡信息
        rollMap.remove(hash);

        // 获取传说卡包
        PackageInfoModel packageInfo = fetchLegendPackageInfo();
        final String packageName = packageInfo.getPackageName();

        // 获取用户当前传说
        CardListDTO currentLegend = fetchReRollTarget(requestUserName, "", packageName, false);
        String currentLegendName = "";
        if (currentLegend != null) {
            currentLegendName = currentLegend.getCardName();
        }
        if (!currentLegendName.equals(request.getReRollCardName())) {
            throw new ResponseException("用户持有的传说卡已发生改变，请重新申请！");
        }

        RollPackageBO packageCardInfo = fetchPackageCardList(packageName);
        List<CardListDTO> destCardList = packageCardInfo.getSpList().stream()
                .filter(c -> !c.getCardName().equals(request.getReRollCardName()))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(destCardList)) {
            throw new ResponseException("[%s]无法重抽，请联系管理员！", packageName);
        }

        CardListDTO destCard = destCardList.get(rd.nextInt(destCardList.size()));
        String resultBuilder = requestUserName + "抽到的传说卡是：\n" +
                PackageUtil.printCard(destCard) + "\n" + getPreviewByName(destCard.getCardName());;

        if (StringUtils.isEmpty(currentLegendName)) {
            try {
                rollService.roll(Collections.singletonList(destCard.getCardName()), requestUserName, operator.getUserName());
            } catch (OperationException oe) {
                throw new ResponseException(oe.getMessage());
            } catch (Exception e) {
                throw new ResponseException("出现未知错误，请联系管理员");
            }
        } else {
            RollRequestBO confirmBO = new RollRequestBO(requestUserName, null, 0, request.getRequest(), RollRequestTypeEnum.LEGEND_CONFIRM);
            confirmBO.setReRollCardName(currentLegendName);
            confirmBO.setReRollDescName(destCard.getCardName());
            String newHash = DigestUtils.md5DigestAsHex(confirmBO.toString().getBytes()).substring(0, 4);
            rollMap.put(newHash, confirmBO);
            resultBuilder += "\n你当前的传说卡为[" + currentLegendName + "]，若确认替换，请执行以下抽卡操作，否则请取消该抽卡操作：\n>抽卡 " + newHash;
        }

        if (AutoConfig.fetchDp()) {
            try {
                int originDp = request.getOriginDp();
                int currentDp = userService.decDuelPoint(requestUserName, 100, operator.getUserName());
                resultBuilder += String.format("\n%s的DP变化：%d->%d", requestUserName, originDp, currentDp);
            } catch (OperationException oe) {
                broadcastFacade.sendMsgAsync(buildResponse(oe.getMessage(), request.getRequest(), true));
            } catch (Exception e) {
                log.error("扣除dp失败：", e);
                broadcastFacade.sendMsgAsync(buildResponse("出现未知错误，请联系管理员", request.getRequest(), true));
            }
        }

        broadcastFacade.sendMsgAsync(buildResponse(resultBuilder, request.getRequest(), true));
        return true;
    }

    /**
     * 确认替换传说
     *
     * @param hash     散列key
     * @param operator 操作人
     * @param response 处理抽卡的消息
     * @return
     */
    private boolean handleLegendConfirm(String hash, UserDataDTO operator, JSONObject response) throws ResponseException {
        RollRequestBO request = rollMap.getOrDefault(hash, null);
        rollMap.remove(hash);
        if (request == null) {
            throw new ResponseException("确认传说请求[%s]不存在！", hash);
        }
        if (request.getType() != RollRequestTypeEnum.LEGEND_CONFIRM) {
            throw new ResponseException("无法处理非传说确认请求[%s]！", hash);
        }
        final String requestUserName = request.getUserName();
        final String reRollCardName = request.getReRollCardName();
        final String reRollDescName = request.getReRollDescName();

        ReplaceCardParam param = new ReplaceCardParam();
        param.setName(operator.getUserName());
        param.setTargetUser(requestUserName);
        param.setOldCardName(reRollCardName);
        param.setNewCardName(reRollDescName);
        param.setCount(1);
        try {
            userService.exchangeOwnCard(param);
        } catch (OperationException oe) {
            throw new ResponseException(oe.getMessage());
        } catch (Exception e) {
            throw new ResponseException("出现未知错误，请联系管理员");
        }

        String resultBuilder = "成功将[" + reRollCardName + "]更改为[" + reRollDescName + "]!";
        broadcastFacade.sendMsgAsync(buildResponse(resultBuilder, request.getRequest(), true));
        return true;
    }

    /**
     * 取消抽卡
     *
     * @param userData 用户数据
     * @param argList  参数列表
     * @param o        请求消息
     */
    private void rollBackRoll(UserDataDTO userData, List<String> argList, JSONObject o) throws ResponseException {
        String hash = argList.get(1);
        refreshRollRequest();
        RollRequestBO request = rollMap.getOrDefault(hash, null);
        if (request == null) {
            throw new ResponseException("该抽卡请求不存在！");
        }
        if (request.getUserName().equals(userData.getUserName()) || (userService.adminCheck(userData.getUserName()))) {
            rollMap.remove(hash);
            broadcastFacade.sendMsgAsync(buildResponse(String.format("抽卡请求[%s]已取消！", hash), o));
        } else {
            throw new ResponseException("你无权取消该抽卡请求！");
        }
    }

    /**
     * 打印抽卡请求
     *
     * @param o
     */
    private void printRollList(JSONObject o) {
        refreshRollRequest();
        StringBuilder sb = new StringBuilder();
        sb.append("最近90分钟内的未处理抽卡请求：");
        if (rollMap.size() == 0) {
            sb.append("\n无");
        } else {
            for (Map.Entry<String, RollRequestBO> entry : rollMap.entrySet()) {
                sb.append("\n(").append(entry.getKey()).append(")").append(entry.getValue().print());
            }
            sb.append("\n管理员可用以下指令处理所有普通抽卡：\n>抽卡 all");
        }
        broadcastFacade.sendMsgAsync(buildResponse(sb.toString(), o));
    }

    /**
     * 更新抽卡请求列表，移除过期请求
     */
    private synchronized void refreshRollRequest() {
        Iterator<Map.Entry<String, RollRequestBO>> iterator = rollMap.entrySet().iterator();
        List<RollRequestBO> removedRequestList = new LinkedList<>();
        while (iterator.hasNext()) {
            Map.Entry<String, RollRequestBO> data = iterator.next();
            long requestTime = data.getValue().getRequestTime();
            if (System.currentTimeMillis() - requestTime > TIME_GAP) {
                removedRequestList.add(data.getValue());
                iterator.remove();
            }
        }
        if (!CollectionUtils.isEmpty(removedRequestList)) {
            StringBuilder sb = new StringBuilder();
            for (RollRequestBO requestBO : removedRequestList) {
                sb.append("你的以下抽卡请求已超时：\n").append(requestBO.print());
                broadcastFacade.sendMsgAsync(buildResponse(sb.toString(), requestBO.getRequest(), true));
                sb.setLength(0);
            }
        }
    }

    /**
     * 用户重抽时，获取重抽对象
     *
     * @param userName 发起用户
     * @param cardName 重抽卡片名
     * @return 重抽卡片对象
     */
    private CardListDTO fetchReRollTarget(String userName, String cardName, String packageName, boolean checkNull) throws ResponseException {
        SearchCardParam param = new SearchCardParam();
        param.setCardName(cardName);
        param.setName(userName);
        param.setRareList(Arrays.asList("SR", "UR", "HR", "GR", "SER"));
        if (!StringUtils.isEmpty(packageName)) {
            param.setPackageNameList(Collections.singletonList(packageName));
        }
        List<String> alterList = cardPreviewService.blurSearch(param.getCardName());
        List<CardListDTO> dtoList = cardService.selectListUser(param, alterList);
        dtoList = dtoList.stream().filter(c -> c.getCount() > 0).collect(Collectors.toList());

        // 查找目标卡片
        CardListDTO targetCard = dtoList.stream().filter(c -> c.getCardName().equals(cardName)).findFirst().orElse(null);
        if (targetCard == null) {
            if (dtoList.isEmpty() && checkNull) {
                throw new ResponseException("找不到需要重抽的卡片！");
            } else if (dtoList.size() > 1) {
                throw new ResponseException("找到多于一张符合条件的卡片，请修改条件重试！");
            } else if (!dtoList.isEmpty()) {
                targetCard = dtoList.get(0);
            }
        }
        if (checkNull) {
            if (PackageUtil.NR_LIST.contains(targetCard.getRare())) {
                throw new ResponseException("非闪卡不可重抽！");
            }
            if (PackageUtil.canNotRoll(targetCard.getPackageName())) {
                throw new ResponseException("SP卡包的卡片不可重抽！");
            }
        }

        return targetCard;
    }

    /**
     * 根据卡包参数获取指定卡包
     *
     * @param packageArg 卡包参数
     * @return 指定卡包
     */
    private PackageInfoModel fetchPackageInfo(String packageArg) throws Exception {
        if (StringUtils.isEmpty(packageArg)) {
            throw new ResponseException("卡包参数为空！");
        }

        // 获取卡包
        List<PackageInfoModel> packageList = packageService.selectByName(packageArg);
        if (CollectionUtils.isEmpty(packageList)) {
            packageList = packageService.selectByDetail(packageArg);
        }
        if (CollectionUtils.isEmpty(packageList)) {
            throw new ResponseException("找不到该卡包！");
        } else if (packageList.size() > 1) {
            throw new ResponseException("找到多于一个卡包，请输入能准确匹配的卡包名！");
        }
        PackageInfoModel pack = packageList.get(0);
        if (PackageUtil.canNotRoll(pack.getPackageName())) {
            throw new ResponseException("该卡包不可抽取！");
        }

        return pack;
    }

    /**
     * 获取传说卡包
     *
     * @return 卡包信息
     */
    private PackageInfoModel fetchLegendPackageInfo() throws ResponseException {
        // 获取卡包
        List<PackageInfoModel> packageList = packageService.selectByName("");
        if (CollectionUtils.isEmpty(packageList)) {
            throw new ResponseException("卡包列表为空，请联系管理员！");
        }
        List<PackageInfoModel> legendList = packageList.stream().filter(p -> PackageUtil.isLegendPackage(p.getPackageName())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(legendList)) {
            throw new ResponseException("找不到该卡包！");
        } else if (legendList.size() > 1) {
            throw new ResponseException("找到多于一个卡包，请输入能准确匹配的卡包名！");
        }

        return legendList.get(0);
    }

    /**
     * 根据卡包参数获取指定卡包的卡片信息
     *
     * @param packageName 卡包名
     * @return 指定卡包的信息
     */
    private RollPackageBO fetchPackageCardList(String packageName) {
        RollPackageBO result = new RollPackageBO();

        SearchCardParam param = new SearchCardParam();
        param.setPackageNameList(Collections.singletonList(packageName));
        List<CardListDTO> cardList = cardService.selectListAdmin(param, null);
        Map<String, List<CardListDTO>> cardMap = cardList.stream()
                .filter(c -> c.getPackageName().equals(packageName))
                .collect(Collectors.groupingBy(CardListDTO::getRare));

        result.getNormalList().addAll(cardMap.getOrDefault("N", Collections.emptyList()));
        result.getRareList().addAll(cardMap.getOrDefault("R", Collections.emptyList()));
        result.getAwardList().addAll(cardMap.getOrDefault("SR", Collections.emptyList()));
        result.getAwardList().addAll(cardMap.getOrDefault("UR", Collections.emptyList()));
        result.getAwardList().addAll(cardMap.getOrDefault("HR", Collections.emptyList()));
        result.getSrList().addAll(cardMap.getOrDefault("SR", Collections.emptyList()));
        result.getUrList().addAll(cardMap.getOrDefault("UR", Collections.emptyList()));
        result.getHrList().addAll(cardMap.getOrDefault("HR", Collections.emptyList()));
        result.getSpList().addAll(cardMap.getOrDefault("GR", Collections.emptyList()));
        result.getSpList().addAll(cardMap.getOrDefault("SER", Collections.emptyList()));

        return result;
    }

    private String getPreviewByName(String cardName) {
        try {
            CardPreviewModel preview = cardPreviewService.selectPreviewByName(cardName);
            if (preview != null) {
                return CardPreviewUtil.getPreview(preview, false);
            }
        } catch (Exception e) {
            log.error("查询卡片出错：", e);
        }
        return "";
    }
}