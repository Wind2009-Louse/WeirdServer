package com.weird.controller;

import com.alibaba.fastjson.JSONObject;
import com.weird.aspect.SearchParamFix;
import com.weird.aspect.TrimArgs;
import com.weird.config.DuelConfig;
import com.weird.model.DuelHistoryModel;
import com.weird.model.enums.LoginTypeEnum;
import com.weird.model.param.DuelHistoryParam;
import com.weird.service.DuelService;
import com.weird.service.UserService;
import com.weird.utils.RequestUtil;
import com.weird.utils.OperationException;
import com.weird.utils.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * 决斗相关Controller
 *
 * @author Nidhogg
 * @date 2021.11.24
 */
@RestController
@TrimArgs
@SearchParamFix
@Slf4j
public class DuelController {
    @Autowired
    DuelService duelService;

    @Autowired
    UserService userService;

    @Autowired
    DuelConfig duelConfig;

    /**
     * 判断入参是秒还是毫秒
     */
    final static long MILL_GAP = 10000000000L;

    @RequestMapping("/duel/message")
    @ResponseBody
    public boolean receiveMessage(HttpServletRequest request) throws IOException, OperationException {
        if (request == null) {
            return false;
        }
        JSONObject o = RequestUtil.getJsonFromRequest(request);

        String verifyKey = o.getString("key");
        if (verifyKey == null || !verifyKey.equals(duelConfig.getKey())) {
            throw new OperationException("校验失败！");
        }

        DuelHistoryModel model = new DuelHistoryModel();
        model.setPlayerA(solvePlayerName(o, "player_a", true));
        model.setPlayerB(solvePlayerName(o, "player_b", true));
        model.setPlayerC(solvePlayerName(o, "player_c", false));
        model.setPlayerD(solvePlayerName(o, "player_d", false));
        model.setScoreA(solveScore(o, "score_a"));
        model.setScoreB(solveScore(o, "score_b"));
        model.setScoreC(solveScore(o, "score_c"));
        model.setScoreD(solveScore(o, "score_d"));
        model.setStartTime(solveTimeStamp(o, "start_time"));
        model.setEndTime(solveTimeStamp(o, "end_time"));
        long duelId = duelService.addDuelHistory(model);
        if (duelId > 0) {
            CompletableFuture.runAsync(() -> {
                // 判断是否超时
                if (model.getEndTime() - model.getStartTime() <= duelConfig.getMinSecond() * 1000) {
                    return;
                }
                boolean isTag = !StringUtils.isEmpty(model.getPlayerC()) && !StringUtils.isEmpty(model.getPlayerD());
                int winDP;
                int lostDP;
                if (isTag) {
                    winDP = duelConfig.getTagWin();
                    lostDP = duelConfig.getTagLost();
                    if (model.getScoreC() == 0 && model.getScoreD() == 0 && (model.getScoreA() >= 0 || model.getScoreB() >= 0)) {
                        model.setScoreC(model.getScoreA());
                        model.setScoreD(model.getScoreB());
                    }
                } else {
                    winDP = duelConfig.getDuoWin();
                    lostDP = duelConfig.getDuoLost();
                }

                // 处理每人结果
                userService.handleDuelResult(duelId, model.getPlayerA(), model.getScoreA(), model.getScoreB(), winDP, lostDP);
                userService.handleDuelResult(duelId, model.getPlayerB(), model.getScoreB(), model.getScoreA(), winDP, lostDP);
                userService.handleDuelResult(duelId, model.getPlayerC(), model.getScoreC(), model.getScoreD(), winDP, lostDP);
                userService.handleDuelResult(duelId, model.getPlayerD(), model.getScoreD(), model.getScoreC(), winDP, lostDP);
            });
        }

        return duelId > 0;
    }

    /**
     * 从报文中获取用户名称
     *
     * @param o         报文
     * @param key       key
     * @param checkNull 是否检查null值
     */
    private String solvePlayerName(JSONObject o, String key, boolean checkNull) throws OperationException {
        String string = o.getString(key);
        if (string == null) {
            if (checkNull) {
                throw new OperationException("[%s]参数不可为空！", key);
            }
            return string;
        }
        int splitIndex = string.indexOf('$');
        if (splitIndex == -1) {
            return string;
        }
        return string.substring(0, splitIndex);
    }

    /**
     * 从报文中获取比分
     *
     * @param o   报文
     * @param key key
     * @return
     */
    private int solveScore(JSONObject o, String key) {
        int integer = o.getIntValue(key);
        if (integer < 0) {
            integer = 0;
        }
        return integer;
    }

    static List<DateFormat> timeFormatList = Arrays.asList(
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"),
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    );

    /**
     * 从报文中获取时间
     *
     * @param o   报文
     * @param key key
     * @return
     */
    private long solveTimeStamp(JSONObject o, String key) throws OperationException {
        String string = o.getString(key);
        // 判断是否为时间戳格式
        for (DateFormat dateFormat : timeFormatList) {
            try {
                Date formattedDate = dateFormat.parse(string);
                Calendar dateCalender = Calendar.getInstance();
                dateCalender.setTime(formattedDate);
                return dateCalender.getTimeInMillis();
            } catch (Exception e) {

            }
        }
        // 判断是秒还是毫秒，以毫秒形式返回
        try {
            long timestamp = Long.parseLong(string);
            if (timestamp > 0 && timestamp <= MILL_GAP) {
                timestamp *= 1000;
            }
            return timestamp;
        } catch (NumberFormatException ne) {
            throw new OperationException("时间格式有误！");
        }
    }

    public PageResult<DuelHistoryModel> searchResult(@RequestBody DuelHistoryParam param) throws Exception {
        // 管理权限验证
        if (userService.checkLogin(param.getName(), param.getPassword()) != LoginTypeEnum.ADMIN) {
            throw new OperationException("权限不足！");
        }
        if (param.getStartTime() > 0 && param.getStartTime() <= MILL_GAP) {
            param.setStartTime(param.getStartTime() * 1000);
        }
        if (param.getEndTime() > 0 && param.getEndTime() <= MILL_GAP) {
            param.setEndTime(param.getEndTime() * 1000);
        }

        List<DuelHistoryModel> resultList = duelService.searchByParam(param);
        PageResult<DuelHistoryModel> result = new PageResult<>();
        result.addPageInfo(resultList, param.getPage(), param.getPageSize());
        return result;
    }
}
