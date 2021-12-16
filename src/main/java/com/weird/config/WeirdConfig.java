package com.weird.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 诡异相关配置
 *
 * @author Nidhogg
 * @date 2021.12.16
 */
@ConfigurationProperties(prefix = "weird")
@Component
@Data
public class WeirdConfig {
    /**
     * 抽卡次数变转盘需要的次数
     */
    static int rollCountToRoulette = 50;
    public static int fetchRollCountToRoulette() {
        return rollCountToRoulette;
    }
    public void setRollCountToRoulette(int rollCountToRoulette) {
        WeirdConfig.rollCountToRoulette = rollCountToRoulette;
    }

    /**
     * 月见黑保底
     */
    static int nonAwardLimit = 100;
    public static int fetchNonAwardLimit() {
        return nonAwardLimit;
    }
    public void setNonAwardLimit(int nonAwardLimit) {
        WeirdConfig.nonAwardLimit = nonAwardLimit;
    }
}
