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
import com.weird.utils.StringExtendUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.weird.utils.BroadcastUtil.buildResponse;

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

    @Override
    public void handle(JSONObject o) throws Exception {
        String message = o.getString("raw_message");
        String userQQ = o.getString("user_id");
        if (message.startsWith(SPLIT_STR)) {
            UserDataDTO userData = userService.getUserByQQ(userQQ);
            if (userData == null) {
                broadcastFacade.sendMsgAsync(buildResponse("你暂未绑定帐号，请私聊使用以下指令进行绑定！\n>认证 用户名 密码", o));
                return;
            }

            String args = message.substring(SPLIT_STR.length()).trim();

            List<String> argList = StringExtendUtil.split(args, " ");
            if (CollectionUtils.isEmpty(argList)) {
                printRollList(o);
            } else if (argList.size() == 1) {
                if ("列表".equals(argList.get(0))) {
                    printRollList(o);
                } else {
                    // 管理员同意抽卡
                    responseRoll(userData, argList.get(0), o);
                }
            } else if (argList.size() == 2) {
                String packageArg = argList.get(0).trim();
                String countArg = argList.get(1).trim();
                if ("取消".equals(packageArg)) {
                    // 取消抽卡请求
                    rollBackRoll(userData, countArg, o);
                } else {
                    // 发起抽卡请求
                    requestRoll(userData, packageArg, countArg, o);
                }
            } else {
                broadcastFacade.sendMsgAsync(buildResponse("请输入正确的格式：\n>抽卡 卡包名 数量", o));
            }
        }
    }

    private void requestRoll(UserDataDTO userData, String packageArg, String countArg, JSONObject o) throws Exception {
        int rollCount = 0;

        try {
            rollCount = Integer.parseInt(countArg);
            if (rollCount <= 0) {
                throw new OperationException("请输入合法的数量！");
            }
        } catch (NumberFormatException | OperationException ne) {
            broadcastFacade.sendMsgAsync(buildResponse("请输入合法的数量！", o));
            return;
        }
        updateRollRequest();
        for (RollRequestBO data : rollMap.values()) {
            if (data.getUserName().equals(userData.getUserName())) {
                broadcastFacade.sendMsgAsync(buildResponse("一个用户只能同时申请一次抽卡！", o));
                return;
            }
        }
        if (rollCount > 50) {
            broadcastFacade.sendMsgAsync(buildResponse("一次申请只能进行最多50次抽卡！", o));
            return;
        }

        List<PackageInfoModel> packageList = packageService.selectByName(packageArg);
        if (packageList.size() == 0) {
            broadcastFacade.sendMsgAsync(buildResponse("找不到该卡包！", o));
            return;
        } else if (packageList.size() > 1) {
            broadcastFacade.sendMsgAsync(buildResponse("找到多于一个卡包，请输入能准确匹配的卡包名！", o));
            return;
        }
        final PackageInfoModel pack = packageList.get(0);

        RollRequestBO requestBO = new RollRequestBO(userData.getUserName(), pack, rollCount);
        String hash = DigestUtils.md5DigestAsHex(requestBO.toString().getBytes()).substring(0, 4);
        rollMap.put(hash, requestBO);
        broadcastFacade.sendMsgAsync(buildResponse(String.format("已成功申请抽取%d包[%s]，管理员可用以下指令进行同意：\n>抽卡 %s",
                rollCount, pack.getPackageName(), hash), o));
    }

    private void responseRoll(UserDataDTO userData, String hash, JSONObject o) {
        if (!userService.adminCheck(userData.getUserName())) {
            broadcastFacade.sendMsgAsync(buildResponse("请输入正确的格式：\n>抽卡 卡包名 数量", o));
            return;
        }

        updateRollRequest();
        RollRequestBO request = rollMap.getOrDefault(hash, null);
        Random rd = new Random();
        if (request != null) {
            int rollCount = request.getRollCount();
            rollMap.remove(hash);
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
                broadcastFacade.sendMsgAsync(buildResponse(String.format("[%s]的卡包配置有误，请联系管理员", request.getPackageInfo().getPackageName()), o));
                return;
            }

            List<List<CardListDTO>> cardResultAllList = new LinkedList<>();
            while (rollCount-- > 0) {
                List<CardListDTO> cardResultList = new LinkedList<>();
                int normalIndex1 = rd.nextInt(normalList.size());
                int normalIndex2 = rd.nextInt(normalList.size());
                while (normalIndex1 == normalIndex2) {
                    normalIndex2 = rd.nextInt(normalList.size());
                }
                cardResultList.add(normalList.get(normalIndex1));
                cardResultList.add(normalList.get(normalIndex2));

                if (rd.nextInt(100) < 4) {
                    // 0-3 闪卡
                    cardResultList.add(awardList.get(rd.nextInt(awardList.size())));
                } else {
                    // 其他 R
                    cardResultList.add(rareList.get(rd.nextInt(rareList.size())));
                }
                cardResultAllList.add(cardResultList);
            }

            StringBuilder resultBuilder = new StringBuilder();
            resultBuilder.append(request.getUserName()).append("的抽卡结果：");
            StringBuilder exceptBuilder = new StringBuilder();
            int resultIndex = 1;
            for (List<CardListDTO> resultList : cardResultAllList) {
                resultBuilder.append("\n").append(resultIndex++).append("：");
                resultBuilder.append(resultList.stream().map(res -> String.format("[%s]%s",
                        res.getRare(), res.getCardName())).collect(Collectors.joining("、")));
                try {
                    rollService.roll(resultList.stream().map(CardListDTO::getCardName).collect(Collectors.toList()), request.getUserName(), userData.getUserName());
                } catch (Exception e) {
                    exceptBuilder.append("\n").append(e.getMessage());
                }
                if (resultIndex % 10 == 1) {
                    broadcastFacade.sendMsgAsync(buildResponse(resultBuilder.toString(), o, true));
                    resultBuilder.setLength(0);
                }
            }

            if (resultBuilder.length() > 0) {
                broadcastFacade.sendMsgAsync(buildResponse(resultBuilder.toString(), o, true));
            }
            String exceptString = exceptBuilder.toString();
            if (!StringUtils.isEmpty(exceptString)) {
                broadcastFacade.sendMsgAsync(buildResponse("出现以下错误，请联系管理员处理：" + exceptString, o, true), 1000);
            }
        }
    }

    private void rollBackRoll(UserDataDTO userData, String hash, JSONObject o) {
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
            broadcastFacade.sendMsgAsync(buildResponse(String.format("你无权取消该抽卡请求！", hash), o));
        }
    }

    private void printRollList(JSONObject o) {
        updateRollRequest();
        StringBuilder sb = new StringBuilder();
        sb.append("最近90分钟内的抽卡请求：");
        if (rollMap.size() == 0) {
            sb.append("\n无");
        } else {
            for (Map.Entry<String, RollRequestBO> entry : rollMap.entrySet()) {
                sb.append(String.format("\n(%s)%s抽%d包[%s]",
                        entry.getKey(),
                        entry.getValue().getUserName(),
                        entry.getValue().getRollCount(),
                        entry.getValue().getPackageInfo().getPackageName()));
            }
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