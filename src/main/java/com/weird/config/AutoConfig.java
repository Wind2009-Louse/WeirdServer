package com.weird.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 自动配置
 *
 * @author Nidhogg
 * @date 2021.12.01
 */
@ConfigurationProperties(prefix = "auto")
@Component
@Data
public class AutoConfig {
    /**
     * 是否使用DP
     */
    static boolean dp = false;
    public static boolean fetchDp() {
        return dp;
    }
    public void setDp(boolean dp) {
        AutoConfig.dp = dp;
    }

    /**
     * 重抽需要使用的卡
     */
    static String reRollCard = "";
    public static String fetchReRollCard() {
        return reRollCard;
    }
    public void setReRollCard(String reRollCard) {
        AutoConfig.reRollCard = reRollCard;
    }

    /**
     * 交换需要使用的卡
     */
    static String exchangeCard = "";
    public static String fetchExchangeCard() {
        return exchangeCard;
    }
    public void setExchangeCard(String exchangeCard) {
        AutoConfig.exchangeCard = exchangeCard;
    }

    /**
     * 随机抽闪使用的卡
     */
    static String awardCard = "";
    public static String fetchAwardCard() {
        return awardCard;
    }
    public void setAwardCard(String awardCard) {
        AutoConfig.awardCard = awardCard;
    }

    /**
     * 每日抽卡获取DP的次数
     */
    static int dailyRollCondition = 4;
    public static int fetchDailyRollCondition() {
        return dailyRollCondition;
    }
    public void setDailyRollCondition(int dailyRollCondition) {
        AutoConfig.dailyRollCondition = dailyRollCondition;
    }

    /**
     * 每日抽卡获取DP的数量
     */
    static int dailyRollDp = 10;
    public static int fetchDailyRollDp() {
        return dailyRollDp;
    }
    public void setDailyRollDp(int dailyRollDp) {
        AutoConfig.dailyRollDp = dailyRollDp;
    }

    /**
     * 每日抽卡出闪时获取DP的数量
     */
    static int dailyRareDp = 10;
    public static int fetchDailyRareDp() {
        return dailyRareDp;
    }
    public void setDailyRareDp(int dailyRareDp) {
        AutoConfig.dailyRareDp = dailyRareDp;
    }

    /**
     * 每周获取交换券需要完成的日常数量
     */
    static int weeklyMissionToExchange = 5;
    public static int fetchWeeklyMissionToExchange() {
        return weeklyMissionToExchange;
    }
    public void setWeeklyMissionToExchange(int weeklyMissionToExchange) {
        AutoConfig.weeklyMissionToExchange = weeklyMissionToExchange;
    }
}
