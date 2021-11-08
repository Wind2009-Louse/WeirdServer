package com.weird.handler.chatimpl;

import com.alibaba.fastjson.JSONObject;
import com.weird.facade.BroadcastFacade;
import com.weird.handler.ChatHandler;
import com.weird.model.bo.RollBroadcastBO;
import com.weird.model.dto.RollListDTO;
import com.weird.model.param.SearchRollParam;
import com.weird.service.RollService;
import com.weird.utils.PageResult;
import com.weird.utils.StringExtendUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.weird.utils.BroadcastUtil.*;

/**
 * 诡异查闪率
 *
 * @author Nidhogg
 * @date 2021.9.30
 */
@Component
@Slf4j
public class ChatRareCheckHandler implements ChatHandler {
    @Autowired
    BroadcastFacade broadcastFacade;

    @Autowired
    RollService rollService;

    final static String SPLIT_STR = ">查闪率";

    final static int REFRESH_GAP = 1000 * 60 * 30;

    final static int SHOW_ALL_GAP = 1000 * 60 * 5;

    static long LAST_SEARCH_DAY_GAP = 0;

    static long LAST_SEARCH_TIME = 0;

    static Map<String, Long> lastPrintAllTimeMap = new HashMap<>();

    List<RollBroadcastBO> userDataList = new LinkedList<>();

    @Override
    public void handle(JSONObject o) {
        String message = o.getString(MESSAGE);
        String groupId = o.getString(GROUP_ID);
        if (!lastPrintAllTimeMap.containsKey(groupId)) {
            lastPrintAllTimeMap.put(groupId, 0L);
        }
        long lastShowAllTime = lastPrintAllTimeMap.getOrDefault(groupId, 0L);
        if (message.startsWith(SPLIT_STR)) {
            String cardArgs = message.substring(SPLIT_STR.length()).trim();
            List<String> argList = StringExtendUtil.split(cardArgs, " ");
            long currentTime = System.currentTimeMillis();
            boolean cacheEnabled = currentTime - LAST_SEARCH_TIME <= REFRESH_GAP;

            // 解析参数
            long queryGap = 7;
            String targetUser = null;
            if (CollectionUtils.isEmpty(argList)) {
                if (cacheEnabled) {
                    queryGap = LAST_SEARCH_DAY_GAP;
                }
            } else {
                String firstArg = argList.get(0);
                if (argList.size() > 1) {
                    targetUser = argList.get(1);
                }
                try {
                    queryGap = Long.parseLong(firstArg);
                } catch (Exception e) {
                    targetUser = firstArg;
                    if (cacheEnabled) {
                        queryGap = LAST_SEARCH_DAY_GAP;
                    }
                }
            }

            // 判断是否刷新缓存
            if (!cacheEnabled) {
                updateCache(queryGap);
            } else if (queryGap != LAST_SEARCH_DAY_GAP) {
                broadcastFacade.sendMsgAsync(buildResponse(String.format("为减轻数据库负担，闪率统计只能半小时刷新一次缓存。当前缓存为%d天内的闪率。", LAST_SEARCH_DAY_GAP), o));
                return;
            }

            // 判断是否显示全部数据
            RollBroadcastBO target = null;
            String responseString;
            if (targetUser != null) {
                String finalTargetUser = targetUser;
                target = userDataList.stream().filter(u -> finalTargetUser.equals(u.getName())).findFirst().orElse(null);
            }
            if (target == null) {
                if (currentTime - lastShowAllTime <= SHOW_ALL_GAP) {
                    broadcastFacade.sendMsgAsync(buildResponse("为避免刷屏，全部闪率统计只能5分钟查询一次。", o));
                    return;
                }
                lastPrintAllTimeMap.put(groupId, currentTime);
                responseString = String.format("%s天内闪率统计\n", LAST_SEARCH_DAY_GAP) + buildRollTable(userDataList, "玩家");
            } else {
                responseString = String.format("%s天内闪率统计\n", LAST_SEARCH_DAY_GAP) + buildRollTable(Collections.singletonList(target), "玩家");
            }
            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            responseString += "\n最后更新时间：" + timeFormat.format(new Date(LAST_SEARCH_TIME));

            broadcastFacade.sendMsgAsync(buildResponse(responseString, o));

        }
    }

    private void updateCache(long day) {
        long currentTime = System.currentTimeMillis();
        try {
            long searchEndTime = currentTime / 1000;
            long searchStartTime = searchEndTime - 86400 * day;
            if (day <= 0 && searchStartTime <= 0) {
                return;
            }

            SearchRollParam searchParam = new SearchRollParam();
            searchParam.setPage(1);
            searchParam.setStartTime(searchStartTime);
            searchParam.setEndTime(searchEndTime);
            searchParam.fix();
            searchParam.setPageSize(0);
            PageResult<RollListDTO> result = rollService.selectRollList(searchParam);

            if (result == null) {
                return;
            }
            List<RollListDTO> dataList = result.getDataList();
            if (CollectionUtils.isEmpty(dataList)) {
                return;
            }

            RollBroadcastBO totalBO = new RollBroadcastBO();
            Map<String, RollBroadcastBO> userBoMap = new HashMap<>();
            Map<String, RollBroadcastBO> deckBoMap = new HashMap<>();
            calculateRollResult(totalBO, userBoMap, deckBoMap, dataList);
            userDataList.clear();
            userDataList.addAll(userBoMap.values());
            userDataList.add(totalBO);

            LAST_SEARCH_TIME = currentTime;
            LAST_SEARCH_DAY_GAP = day;
        } catch (Exception e) {
            log.error("刷新闪率缓存时失败：", e);
        }
    }
}
