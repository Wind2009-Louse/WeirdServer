package com.weird.handler.chatimpl;

import com.weird.facade.BroadcastFacade;
import com.weird.handler.ChatHandler;
import com.weird.model.bo.RollBroadcastBO;
import com.weird.model.dto.RollListDTO;
import com.weird.model.param.SearchRollParam;
import com.weird.service.RollService;
import com.weird.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.weird.utils.BroadcastUtil.buildRollTable;
import static com.weird.utils.BroadcastUtil.calculateRollResult;

/**
 * 诡异查闪率
 *
 * @author Nidhogg
 * @date 2021.9.30
 */
@Component
public class ChatRareCheckHandler implements ChatHandler {
    @Autowired
    BroadcastFacade broadcastFacade;

    @Autowired
    RollService rollService;

    final static String SPLIT_STR = ">查闪率 ";

    static long lastSearchTime = 0;

    @Override
    public void handle(String message) {
        if (message.startsWith(SPLIT_STR)) {
            String cardArgs = message.substring(SPLIT_STR.length()).trim();
            if (StringUtils.isEmpty(cardArgs)) {
                return;
            }
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastSearchTime <= 1000 * 60 * 30) {
                broadcastFacade.sendMsgAsync("为避免数据库负担，闪率统计只能半小时执行一次。");
                return;
            }
            try {
                long day = Long.parseLong(cardArgs);
                long searchEndTime = currentTime / 1000;
                long searchStartTime = searchEndTime - 86400 * day;
                if (day <= 0 && searchStartTime <= 0) {
                    return;
                }

                SearchRollParam searchParam = new SearchRollParam();
                searchParam.setPageSize(65536);
                searchParam.setPage(1);
                searchParam.setStartTime(searchStartTime);
                searchParam.setEndTime(searchEndTime);
                searchParam.fix();
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
                List<RollBroadcastBO> userDataList = new ArrayList<>(userBoMap.values());
                userDataList.add(totalBO);
                broadcastFacade.sendMsgAsync(String.format("%s天内闪率统计\n", day) + buildRollTable(userDataList, "玩家"));

                lastSearchTime = currentTime;
            } catch (Exception e) {

            }
        }
    }
}
