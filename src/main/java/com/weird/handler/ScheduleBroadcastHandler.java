package com.weird.handler;

import com.weird.model.bo.RollBroadcastBO;
import com.weird.model.dto.RollListDTO;
import com.weird.model.param.SearchRollParam;
import com.weird.service.RollService;
import com.weird.utils.BroadcastBotUtil;
import com.weird.utils.PackageUtil;
import com.weird.utils.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 广播Schedule
 *
 * @author Nidhogg
 * @date 2021.9.26
 */
@Configuration
@EnableAsync
@Slf4j
public class ScheduleBroadcastHandler {
    @Autowired
    RollService rollService;

    @Autowired
    BroadcastBotUtil broadcastBotUtil;

    static final String BROADCAST_DAILY_BEGIN = "午间广播开始啦！";
    static final String BROADCAST_DAILY_ALL = "%s最近两周，大家一共抽了%s包卡，其中%d包出了闪，闪率为%.2f%%！";
    static final String BROADCAST_DAILY_MOST_ROLL = "最勤奋的玩家是%s，这些天抽了%s包之多，其中有%d包出闪，闪率为%.2f%%！";
    static final String BROADCAST_DAILY_BEST_ROLL = "最狗的玩家是%s，竟然在了%s包卡中出了%d张闪，闪率高达%.2f%%！非人哉！";
    static final String BROADCAST_DAILY_WORST_ROLL = "最黑的玩家是%s，最近的%s包卡中只有%d张闪，闪率只有可怜的%.2f%%！不过不要灰心，再接再厉！";
    static final String BROADCAST_DAILY_HOT_PACKAGE = "最近最受欢迎的卡包是%s，这段时间里已经卖出了%d包！";
    static final String BROADCAST_DAILY_BEST_PACKAGE = "出货率最高的卡包是%s，大家在%s包中抽到了%d张闪，足足%.2f%%的闪率！";
    static final String BROADCAST_DAILY_WORST_PACKAGE = "而出货率最低的卡包是%s，拼命抽了%s包，却只有%d张闪，闪率区区%.2f%%！";
    static final String BROADCAST_DAILY_END = "午间广播结束，感谢大家的收听，再见！";

    static final String BROADCAST_WEEKLY = "周日到了，百八也重置了，大家不要忘记了！";

    static final String BROADCAST_MONTHLY_BEGIN = "一个月过去了，一起回顾一下过去一个月的抽卡记录吧！";
    static final String BROADCAST_MONTHLY_ALL = "%s过去一个月，大家一共抽了%s包卡，其中%d包出了闪，闪率为%.2f%%！";
    static final String BROADCAST_MONTHLY_END = "大家下个月再见！";

    /**
     * 百八提醒
     */
    @Async
    @Scheduled(cron = "0 0 0 * * 7")
    public void weeklyMoreRareBroadcast() {
        broadcastBotUtil.sendMsgAsync(BROADCAST_WEEKLY);
    }

    /**
     * 每天广播最近14天数据
     */
    @Async
    @Scheduled(cron = "0 0 12 * * ?")
    public void dailyBroadcast() throws Exception {
        SearchRollParam searchParam = new SearchRollParam();
        searchParam.setPageSize(65536);
        searchParam.setPage(1);
        long searchEndTime = System.currentTimeMillis() / 1000;
        long searchStartTime = searchEndTime - 86400 * 14;
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

        List<String> broadcastList = new ArrayList<>(9);
        broadcastList.add(BROADCAST_DAILY_BEGIN);
        broadcastList.add(putDataToFormat(BROADCAST_DAILY_ALL, totalBO));
        broadcastList.add(getStringByMost(BROADCAST_DAILY_MOST_ROLL, userBoMap.values(), RollBroadcastBO::getTotalCount, false));
        broadcastList.add(getStringByMost(BROADCAST_DAILY_BEST_ROLL, userBoMap.values(), RollBroadcastBO::getRareRate, false));
        broadcastList.add(getStringByMost(BROADCAST_DAILY_WORST_ROLL, userBoMap.values(), o -> -o.getRareRate(), false));
        broadcastList.add(getStringByMost(BROADCAST_DAILY_HOT_PACKAGE, deckBoMap.values(), RollBroadcastBO::getTotalCount, true));
        broadcastList.add(getStringByMost(BROADCAST_DAILY_BEST_PACKAGE, deckBoMap.values(), RollBroadcastBO::getRareRate, false));
        broadcastList.add(getStringByMost(BROADCAST_DAILY_WORST_PACKAGE, deckBoMap.values(), o -> -o.getRareRate(), false));
        broadcastList.add(BROADCAST_DAILY_END);

        beginBroadcast(broadcastList);
    }

    /**
     * 每月广播上一个月统计
     */
    @Async
    @Scheduled(cron = "0 0 18 1 * ?")
    public void monthlyBroadcast() throws Exception {
        SearchRollParam searchParam = new SearchRollParam();
        searchParam.setPageSize(65536);
        searchParam.setPage(1);

        DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-01 00:00:00");
        Date firstDayOfThisMonth = timeFormat.parse(timeFormat.format(new Date(System.currentTimeMillis())));
        Calendar dateCalender = Calendar.getInstance();
        dateCalender.setTime(firstDayOfThisMonth);
        dateCalender.add(Calendar.MONTH, -1);

        long searchStartTime = dateCalender.getTimeInMillis() / 1000;
        long searchEndTime = firstDayOfThisMonth.getTime() / 1000;
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

        List<String> broadcastList = new ArrayList<>(9);
        broadcastList.add(BROADCAST_MONTHLY_BEGIN);
        broadcastList.add(putDataToFormat(BROADCAST_MONTHLY_ALL, totalBO));
        broadcastList.add(getStringByMost(BROADCAST_DAILY_MOST_ROLL, userBoMap.values(), RollBroadcastBO::getTotalCount, false));
        broadcastList.add(getStringByMost(BROADCAST_DAILY_BEST_ROLL, userBoMap.values(), RollBroadcastBO::getRareRate, false));
        broadcastList.add(getStringByMost(BROADCAST_DAILY_WORST_ROLL, userBoMap.values(), o -> -o.getRareRate(), false));
        broadcastList.add(getStringByMost(BROADCAST_DAILY_HOT_PACKAGE, deckBoMap.values(), RollBroadcastBO::getTotalCount, true));
        broadcastList.add(getStringByMost(BROADCAST_DAILY_BEST_PACKAGE, deckBoMap.values(), RollBroadcastBO::getRareRate, false));
        broadcastList.add(getStringByMost(BROADCAST_DAILY_WORST_PACKAGE, deckBoMap.values(), o -> -o.getRareRate(), false));
        broadcastList.add(BROADCAST_MONTHLY_END);

        beginBroadcast(broadcastList);
    }

    /**
     * 根据查询到的抽卡信息，统计抽卡数据
     *
     * @param totalBO   总计数据
     * @param userBoMap 用户数据Map
     * @param deckBoMap 卡包数据Map
     * @param dataList  总抽卡数据
     */
    private void calculateRollResult(
            RollBroadcastBO totalBO,
            Map<String, RollBroadcastBO> userBoMap,
            Map<String, RollBroadcastBO> deckBoMap,
            List<RollListDTO> dataList
    ) {
        for (RollListDTO dto : dataList) {
            if (dto.getIsDisabled() >= 1 || dto.getRollResult().size() != 3) {
                continue;
            }
            boolean isRare = dto.getRollResult().stream().anyMatch(o -> !PackageUtil.NR_LIST.contains(o.getRare()));
            RollBroadcastBO userBO = userBoMap.getOrDefault(dto.getRollUserName(), null);
            if (userBO == null) {
                userBO = new RollBroadcastBO(dto.getRollUserName());
                userBoMap.put(dto.getRollUserName(), userBO);
            }
            RollBroadcastBO deckBO = deckBoMap.getOrDefault(dto.getRollPackageName(), null);
            if (deckBO == null) {
                deckBO = new RollBroadcastBO(String.format("[%s]", dto.getRollPackageName()));
                deckBoMap.put(dto.getRollPackageName(), deckBO);
            }
            RollBroadcastBO[] boList = {totalBO, userBO, deckBO};
            for (RollBroadcastBO bo : boList) {
                bo.setTotalCount(bo.getTotalCount() + 1);
                if (isRare) {
                    bo.setRareCount(bo.getRareCount() + 1);
                }
            }
        }
    }

    /**
     * 根据方法查出最高的个体，并输出广播信息
     *
     * @param formatString   广播信息
     * @param boList         个体列表
     * @param getter         对比方法
     * @param onlyTotalCount 是否只输出总数，不输出闪率统计
     * @return 广播信息
     */
    private String getStringByMost(
            String formatString,
            Collection<RollBroadcastBO> boList,
            Function<RollBroadcastBO, Number> getter,
            boolean onlyTotalCount) {
        RollBroadcastBO target = null;
        Number best = null;
        List<RollBroadcastBO> zeroTargetList = boList.stream().filter(o -> o.getRareCount() == 0).collect(Collectors.toList());
        for (RollBroadcastBO bo : boList) {
            Number currentNum = getter.apply(bo);
            if (target == null || currentNum.doubleValue() > best.floatValue()) {
                target = bo;
                best = currentNum;
            }
        }
        if (target == null) {
            return "";
        }
        if (onlyTotalCount) {
            return String.format(formatString, target.getName(), target.getTotalCount());
        } else {
            if (0 == best.intValue() && !CollectionUtils.isEmpty(zeroTargetList)) {
                return putZeroDataToFormat(formatString, zeroTargetList);
            } else {
                return putDataToFormat(formatString, target);
            }
        }
    }

    /**
     * 将数据进行格式化，返回广播信息
     *
     * @param formatString 广播信息
     * @param target       数据
     * @return 格式化后的广播信息
     */
    private String putDataToFormat(String formatString, RollBroadcastBO target) {
        if (target == null) {
            return "";
        }
        return String.format(formatString,
                target.getName(),
                Long.toString(target.getTotalCount()),
                target.getRareCount(),
                target.getRareRate());
    }

    /**
     * 将数据列表进行格式化，返回广播信息
     *
     * @param formatString 广播信息
     * @param targetList   数据列表
     * @return 格式化后的广播信息
     */
    private String putZeroDataToFormat(String formatString, List<RollBroadcastBO> targetList) {
        if (CollectionUtils.isEmpty(targetList)) {
            return "";
        }
        String nameList = targetList.stream().map(RollBroadcastBO::getName).collect(Collectors.joining("/"));
        String countList = targetList.stream().map(o -> Long.toString(o.getTotalCount())).collect(Collectors.joining("/"));
        return String.format(formatString,
                nameList,
                countList,
                0,
                0f);
    }

    /**
     * 进行异步广播
     *
     * @param broadcastList 广播内容列表
     */
    private void beginBroadcast(List<String> broadcastList) {
        CompletableFuture.runAsync(() -> {
            for (String broadcast : broadcastList) {
                broadcastBotUtil.sendMsgAsync(broadcast);
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {

                }
            }
        });
    }
}
