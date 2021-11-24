package com.weird.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 决斗配置
 *
 * @author Nidhogg
 * @date 2021.11.24
 */
@ConfigurationProperties(prefix = "duel")
@Component
@Data
public class DuelConfig {
    /**
     * 计算入结果的最短时间
     */
    int minSecond = 180;

    /**
     * 是否使用DP
     */
    boolean dp = false;

    /**
     * 校验密钥
     */
    String key = "WS";

    /**
     * 单打胜利DP
     */
    int duoWin = 3;

    /**
     * 单打失败DP
     */
    int duoLost = 2;

    /**
     * 双打胜利DP
     */
    int tagWin = 5;

    /**
     * 双打失败DP
     */
    int tagLost = 3;
}
