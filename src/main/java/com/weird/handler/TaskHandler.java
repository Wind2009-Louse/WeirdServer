package com.weird.handler;

import com.weird.model.bo.RollBroadcastBO;
import com.weird.model.dto.RollListDTO;
import com.weird.model.param.SearchRollParam;
import com.weird.service.RecordService;
import com.weird.service.RollService;
import com.weird.service.TaskService;
import com.weird.utils.BroadcastBotUtil;
import com.weird.utils.CacheUtil;
import com.weird.utils.PackageUtil;
import com.weird.utils.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * 定时任务Handler
 *
 * @author Nidhogg
 * @date 2020.9.9
 */
@Configuration
@EnableAsync
@Slf4j
public class TaskHandler {
    @Autowired
    TaskService taskService;

    @Autowired
    RecordService recordService;

    @Autowired
    RollService rollService;

    @Autowired
    BroadcastBotUtil broadcastBotUtil;

    static final String BROADCAST_BEGIN = "午间广播开始啦！";
    static final String BROADCAST_ALL = "%s最近30天内，大家一共抽了%d包卡，其中%d包出了闪，闪率为%.2f%%！";
    static final String BROADCAST_MOST_ROLL = "最勤奋的玩家是%s，这些天抽了%d包之多，其中有%d包出闪，闪率为%.2f%%！";
    static final String BROADCAST_BEST_ROLL = "最狗的玩家是%s，竟然在了%d包卡中出了%d张闪，闪率高达为%.2f%%！非人哉！";
    static final String BROADCAST_WORST_ROLL = "最黑的玩家是%s，最近的%d包卡中只有%d张闪，闪率只有可怜的%.2f%%！不过不要灰心，再接再厉！";
    static final String BROADCAST_HOT_PACKAGE = "最近最受欢迎的卡包是[%s]，这段时间里已经卖出了%d包！";
    static final String BROADCAST_BEST_PACKAGE = "出货率最高的卡包是[%s]，大家在%d包中抽到了%d张闪，足足%.2f%%的闪率！";
    static final String BROADCAST_WORST_PACKAGE = "而出货率最低的卡包是[%s]，拼命抽了%d包，却只有%d张闪，闪率区区%.2f%%！";
    static final String BROADCAST_END = "午间广播结束，感谢大家的收听，再见！";

    static final String BROADCAST_WEEKLY = "周日到了，百八也重置了，大家不要忘记了！";

    @Async
    @Scheduled(cron = "0 0 0 * * ?")
    public void clearDaily() throws Exception {
        recordService.setRecord("日常更新", "【日常刷新】开始");
        int updateCount = taskService.updateDaily();
        recordService.setRecord("日常更新", "【日常刷新】更新%d条数据", updateCount);
        CacheUtil.clearRollListWithDetailCache();
        CacheUtil.clearCardOwnListCache();
    }

    @Async
    @Scheduled(cron = "0 0 0 ? * 1")
    public void clearWeekly() throws Exception {
        recordService.setRecord("周常更新", "【周常刷新】开始");
        int updateCount = taskService.updateWeekly();
        recordService.setRecord("周常更新", "【周常刷新】更新%d条记录", updateCount);
    }

    @Async
    @Scheduled(cron = "30 0 0 * * ?")
    public void backupDataBase() throws Exception {
        recordService.setRecord("数据库备份", "【数据库备份】开始");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        String newDateString = formatter.format(currentDate);

        Calendar oldDateCalender = Calendar.getInstance();
        oldDateCalender.setTime(currentDate);
        oldDateCalender.add(Calendar.DATE, -14);
        String oldDateString = formatter.format(oldDateCalender.getTime());
        if (Files.deleteIfExists(Paths.get(String.format("backup/data-%s.db", oldDateString)))) {
            recordService.setRecord("数据库备份", "【数据库备份】清除%s时备份的数据库", oldDateString);
        }

        File dir = new File("backup");
        if (!dir.exists()) {
            dir.mkdir();
        }
        Path source = Paths.get("data.db");
        Files.copy(source, new FileOutputStream(String.format("backup/data-%s.db", newDateString)));
        recordService.setRecord("数据库备份", "【数据库备份】结束");
    }

    @Async
    @Scheduled(cron = "0 0 12 * * ?")
    public void dailyBroadcast() throws Exception {
        SearchRollParam searchParam = new SearchRollParam();
        searchParam.setPageSize(65536);
        searchParam.setPage(1);
        long searchEndTime = System.currentTimeMillis() / 1000;
        long searchStartTime = searchEndTime - 2592000;
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
                deckBO = new RollBroadcastBO(dto.getRollPackageName());
                deckBoMap.put(dto.getRollPackageName(), deckBO);
            }
            addBoCount(isRare, totalBO, userBO, deckBO);
        }

        List<String> broadcastList = new ArrayList<>(9);
        broadcastList.add(BROADCAST_BEGIN);
        broadcastList.add(putDataToFormat(BROADCAST_ALL, totalBO));
        broadcastList.add(getStringByMost(BROADCAST_MOST_ROLL, userBoMap.values(), RollBroadcastBO::getTotalCount, false));
        broadcastList.add(getStringByMost(BROADCAST_BEST_ROLL, userBoMap.values(), RollBroadcastBO::getRareRate, false));
        broadcastList.add(getStringByMost(BROADCAST_WORST_ROLL, userBoMap.values(), o -> -o.getRareRate(), false));
        broadcastList.add(getStringByMost(BROADCAST_HOT_PACKAGE, deckBoMap.values(), RollBroadcastBO::getTotalCount, true));
        broadcastList.add(getStringByMost(BROADCAST_BEST_PACKAGE, deckBoMap.values(), RollBroadcastBO::getRareRate, false));
        broadcastList.add(getStringByMost(BROADCAST_WORST_PACKAGE, deckBoMap.values(), o -> -o.getRareRate(), false));
        broadcastList.add(BROADCAST_END);

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

    @Async
    @Scheduled(cron = "0 0 0 * * 7")
    public void weeklyMoreRareBroadcast() {
        broadcastBotUtil.sendMsgAsync(BROADCAST_WEEKLY);
    }

    private void addBoCount(boolean isRare, RollBroadcastBO... boList) {
        if (boList == null) {
            return;
        }
        for (RollBroadcastBO bo : boList) {
            bo.setTotalCount(bo.getTotalCount() + 1);
            if (isRare) {
                bo.setRareCount(bo.getRareCount() + 1);
            }
        }
    }

    private String getStringByMost(
            String formatString,
            Collection<RollBroadcastBO> boList,
            Function<RollBroadcastBO, Number> getter,
            boolean onlyTotalCount) {
        RollBroadcastBO target = null;
        Number best = null;
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
            return putDataToFormat(formatString, target);
        }
    }

    private String putDataToFormat(String formatString, RollBroadcastBO target) {
        if (target == null) {
            return "";
        }
        return String.format(formatString,
                target.getName(),
                target.getTotalCount(),
                target.getRareCount(),
                target.getRareRate());
    }

    @PreDestroy
    public void beforeShutdown() {
        recordService.setRecord("system", "后端进程结束");
    }
}
