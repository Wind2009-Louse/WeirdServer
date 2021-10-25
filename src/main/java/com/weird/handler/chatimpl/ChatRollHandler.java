package com.weird.handler.chatimpl;

import com.alibaba.fastjson.JSONObject;
import com.weird.facade.BroadcastFacade;
import com.weird.handler.ChatHandler;
import com.weird.model.PackageInfoModel;
import com.weird.model.bo.RollRequestBO;
import com.weird.model.dto.CardListDTO;
import com.weird.model.dto.UserDataDTO;
import com.weird.model.param.SearchCardParam;
import com.weird.service.CardService;
import com.weird.service.PackageService;
import com.weird.service.RollService;
import com.weird.service.UserService;
import com.weird.utils.OperationException;
import com.weird.utils.PackageUtil;
import com.weird.utils.StringExtendUtil;
import lombok.extern.slf4j.Slf4j;
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

    static Map<String, RollRequestBO> rollMap = new HashMap<>();

    final static String SPLIT_STR = ">抽卡";

    final static long TIME_GAP = 1000 * 60 * 90;

    final static String ARG_LIST = "列表";

    final static String ARG_CANCEL = "取消";

    final static String ARG_RARE_TO_STOP = "闪停";

    final static String ARG_SHOW_ALL = "全";

    final static List<String> METHOD_ROLL_ALL_LIST = Arrays.asList("all", "ALL", "全部", "全");

    @Override
    public void handle(JSONObject o) throws Exception {
        String message = o.getString("raw_message");
        String userQQ = o.getString("user_id");
        if (message.startsWith(SPLIT_STR)) {
            UserDataDTO userData = userService.getUserByQQ(userQQ);
            if (userData == null) {
                broadcastFacade.sendMsgAsync(buildResponse(NOT_BIND_WARNING, o));
                return;
            }

            String args = message.substring(SPLIT_STR.length()).trim();

            List<String> argList = StringExtendUtil.split(args, " ");
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
                    updateRollRequest();
                    responseRoll(userData, argList, o);
                }
            } else {
                String firstArg = argList.get(0).trim();
                if (ARG_CANCEL.equals(firstArg)) {
                    // 取消抽卡请求
                    rollBackRoll(userData, argList, o);
                } else {
                    // 发起抽卡请求
                    requestRoll(userData, argList, o);
                }
            }
        }
    }

    private void requestRoll(UserDataDTO userData, List<String> argList, JSONObject o) throws Exception {
        String userName = userData.getUserName();
        if (userService.adminCheck(userName)) {
            broadcastFacade.sendMsgAsync(buildResponse("管理员不可发起抽卡请求！", o));
            return;
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
            broadcastFacade.sendMsgAsync(buildResponse("请输入合法的数量！", o));
            return;
        }

        // 判断抽卡合法性
        updateRollRequest();
        for (RollRequestBO data : rollMap.values()) {
            if (data.getUserName().equals(userName)) {
                broadcastFacade.sendMsgAsync(buildResponse("一个用户只能同时申请一次抽卡！", o));
                return;
            }
        }
        if (rollCount > 50) {
            broadcastFacade.sendMsgAsync(buildResponse("一次申请只能进行最多50次抽卡！", o));
            return;
        }

        // 获取卡包
        List<PackageInfoModel> packageList = packageService.selectByName(packageArg);
        if (CollectionUtils.isEmpty(packageList)) {
            packageList = packageService.selectByDetail(packageArg);
        }
        if (CollectionUtils.isEmpty(packageList)) {
            broadcastFacade.sendMsgAsync(buildResponse("找不到该卡包！", o));
            return;
        } else if (packageList.size() > 1) {
            broadcastFacade.sendMsgAsync(buildResponse("找到多于一个卡包，请输入能准确匹配的卡包名！", o));
            return;
        }
        final PackageInfoModel pack = packageList.get(0);
        if (PackageUtil.canNotRoll(pack.getPackageName())) {
            broadcastFacade.sendMsgAsync(buildResponse("该卡包不可抽取！", o));
        }

        RollRequestBO requestBO = new RollRequestBO(userName, pack, rollCount, o);
        String remark = "";
        if (otherArgList.contains(ARG_RARE_TO_STOP)) {
            requestBO.setRareToStop(true);
            remark = "(闪停)";
        }
        if (otherArgList.contains(ARG_SHOW_ALL)) {
            requestBO.setShowAll(true);
        }
        String hash = DigestUtils.md5DigestAsHex(requestBO.toString().getBytes()).substring(0, 4);
        rollMap.put(hash, requestBO);
        broadcastFacade.sendMsgAsync(buildResponse(String.format("已成功申请抽取%d包[%s]%s，管理员可用以下指令进行同意：\n>抽卡 %s",
                rollCount, pack.getPackageName(), remark, hash), o));
    }

    private synchronized void responseRoll(UserDataDTO userData, List<String> argList, JSONObject o) {
        if (!userService.adminCheck(userData.getUserName())) {
            broadcastFacade.sendMsgAsync(buildResponse("请输入正确的格式：\n>抽卡 卡包名 数量", o));
            return;
        }

        String arg = argList.get(0);
        if (METHOD_ROLL_ALL_LIST.contains(arg)) {
            List<String> hashList = new LinkedList<>(rollMap.keySet());
            int successCount = 0;
            for (String hash : hashList) {
                if (handleRollRequest(hash, userData, o)) {
                    successCount++;
                }
            }
            broadcastFacade.sendMsgAsync(buildResponse(String.format("成功处理%d条抽卡请求！", successCount), o, true));
        } else {
            handleRollRequest(arg, userData, o);
        }
    }

    private boolean handleRollRequest(String hash, UserDataDTO operator, JSONObject response) {
        RollRequestBO request = rollMap.getOrDefault(hash, null);
        Random rd = new Random();
        if (request == null) {
            broadcastFacade.sendMsgAsync(buildResponse(String.format("抽卡请求[%s]不存在！", hash), response));
            return false;
        }
        final String requestUserName = request.getUserName();

        // 获取抽卡信息
        int remainRollCount = request.getRollCount();
        int remainDoubleRareCount = userService.getDoubleRareCount(requestUserName);
        int originDoubleRareCount = remainDoubleRareCount;
        rollMap.remove(hash);

        // 查询卡包卡片内容
        SearchCardParam param = new SearchCardParam();
        param.setPackageNameList(Collections.singletonList(request.getPackageInfo().getPackageName()));
        List<CardListDTO> cardList = cardService.selectListAdmin(param, null);
        Map<String, List<CardListDTO>> cardMap = cardList.stream().collect(Collectors.groupingBy(CardListDTO::getRare));

        List<CardListDTO> normalList = cardMap.getOrDefault("N", Collections.emptyList());
        List<CardListDTO> rareList = cardMap.getOrDefault("R", Collections.emptyList());
        List<CardListDTO> awardList = new LinkedList<>();
        awardList.addAll(cardMap.getOrDefault("SR", Collections.emptyList()));
        awardList.addAll(cardMap.getOrDefault("UR", Collections.emptyList()));
        awardList.addAll(cardMap.getOrDefault("HR", Collections.emptyList()));
        if (normalList.size() < 2 || rareList.size() <= 0 || awardList.size() <= 0) {
            broadcastFacade.sendMsgAsync(buildResponse(String.format("[%s]的卡包配置有误，请联系管理员", request.getPackageInfo().getPackageName()), response));
            return false;
        }

        // 记录抽卡结果
        List<List<CardListDTO>> cardResultAllList = new LinkedList<>();
        int totalRateCount = 0;
        int resultIndex = 1;
        int printIndex = 0;
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append(requestUserName).append("的抽卡结果：");

        while (remainRollCount-- > 0) {
            // 生成抽卡内容
            List<CardListDTO> cardResultList = new LinkedList<>();
            int normalIndex1 = rd.nextInt(normalList.size());
            int normalIndex2 = rd.nextInt(normalList.size());
            while (normalIndex1 == normalIndex2) {
                normalIndex2 = rd.nextInt(normalList.size());
            }
            cardResultList.add(normalList.get(normalIndex1));
            cardResultList.add(normalList.get(normalIndex2));
            int rareRate = PackageUtil.NORMAL_RARE_RATE;
            if (remainDoubleRareCount > 0) {
                rareRate = PackageUtil.DOUBLE_RARE_RATE;
                remainDoubleRareCount --;
            }

            boolean currentRare = rd.nextInt(100) < rareRate;
            if (currentRare) {
                // 0-3 闪卡
                cardResultList.add(awardList.get(rd.nextInt(awardList.size())));
                totalRateCount++;
            } else {
                // 其他 R
                cardResultList.add(rareList.get(rd.nextInt(rareList.size())));
            }
            cardResultAllList.add(cardResultList);

            // 生成消息
            if (currentRare || request.isShowAll()) {
                resultBuilder.append("\n").append(resultIndex);
                if (rareRate == PackageUtil.DOUBLE_RARE_RATE) {
                    resultBuilder.append("[百八]");
                }
                resultBuilder.append("：");
                resultBuilder.append(cardResultList.stream().map(res -> {
                    if (PackageUtil.NR_LIST.contains(res.getRare())) {
                        return String.format("[%s]%s", res.getRare(), res.getCardName());
                    } else {
                        return String.format("【%s】%s", res.getRare(), res.getCardName());
                    }
                }).collect(Collectors.joining("、")));
                printIndex++;
                if (printIndex % 10 == 0) {
                    broadcastFacade.sendMsgAsync(buildResponse(resultBuilder.toString(), request.getRequest(), true));
                    resultBuilder.setLength(0);
                }
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

        long totalRollCount = cardResultAllList.size();
        if (totalRateCount == 0) {
            if (request.isShowAll()) {
                resultBuilder.append("\n真是可惜，并没有出闪！");
            } else {
                resultBuilder.append("\n真是可惜，").append(totalRollCount).append("包卡里一张闪都没有！");
            }
        } else if (totalRateCount == 1 && totalRollCount > totalRateCount) {
            if (!request.isShowAll()) {
                resultBuilder.append("\n").append(totalRollCount).append("包卡里出了1张闪，不错，很有精神！");
            }
        } else if (totalRateCount > 1 || totalRollCount == totalRateCount) {
            resultBuilder.append("\n难道是抽卡机出了什么问题？");
            if (!request.isShowAll()) {
                resultBuilder.append(totalRollCount).append("包里可以出").append(totalRateCount).append("张闪的吗？");
            }
        }
        if (!request.isShowAll() && totalRollCount > totalRateCount) {
            resultBuilder.append("\n具体的抽卡结果请前往诡异云查看。");
        }

        if (resultBuilder.length() > 0) {
            broadcastFacade.sendMsgAsync(buildResponse(resultBuilder.toString(), request.getRequest(), true));
        }
        String exceptString = exceptBuilder.toString();
        if (!StringUtils.isEmpty(exceptString)) {
            broadcastFacade.sendMsgAsync(buildResponse("出现以下错误，请联系管理员处理：" + exceptString, response, true), 1000);
        }

        return totalRollCount > 0;
    }

    private void rollBackRoll(UserDataDTO userData, List<String> argList, JSONObject o) {
        String hash = argList.get(1);
        updateRollRequest();
        RollRequestBO request = rollMap.getOrDefault(hash, null);
        if (request == null) {
            broadcastFacade.sendMsgAsync(buildResponse("该抽卡请求不存在！", o));
            return;
        }
        if (request.getUserName().equals(userData.getUserName()) || (userService.adminCheck(userData.getUserName()))) {
            rollMap.remove(hash);
            broadcastFacade.sendMsgAsync(buildResponse(String.format("抽卡请求[%s]已取消！", hash), o));
        } else {
            broadcastFacade.sendMsgAsync(buildResponse("你无权取消该抽卡请求！", o));
        }
    }

    private void printRollList(JSONObject o) {
        updateRollRequest();
        StringBuilder sb = new StringBuilder();
        sb.append("最近90分钟内的未处理抽卡请求：");
        if (rollMap.size() == 0) {
            sb.append("\n无");
        } else {
            for (Map.Entry<String, RollRequestBO> entry : rollMap.entrySet()) {
                sb.append(String.format("\n(%s)%s:%s抽%d包[%s]",
                        entry.getKey(),
                        TIME_FORMAT.format(new Date(entry.getValue().getRequestTime())),
                        entry.getValue().getUserName(),
                        entry.getValue().getRollCount(),
                        entry.getValue().getPackageInfo().getPackageName()));
                if (entry.getValue().isRareToStop()) {
                    sb.append("，闪停");
                }
                if (entry.getValue().isShowAll()) {
                    sb.append("(全)");
                }
            }
            sb.append("\n管理员可用以下指令处理所有抽卡：\n>抽卡 all");
        }
        broadcastFacade.sendMsgAsync(buildResponse(sb.toString(), o));
    }

    private synchronized void updateRollRequest() {
        Iterator<Map.Entry<String, RollRequestBO>> iterator = rollMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, RollRequestBO> data = iterator.next();
            long requestTime = data.getValue().getRequestTime();
            if (System.currentTimeMillis() - requestTime > TIME_GAP) {
                iterator.remove();
            }
        }
    }
}