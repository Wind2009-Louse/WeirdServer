package com.weird.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 决斗历史记录
 *
 * @author Nidhogg
 * @date 2021.11.24
 */
@Data
public class DuelHistoryModel implements Serializable {
    /**
     * 主键
     */
    long duelId;

    /**
     * 用户a
     */
    String playerA;

    /**
     * 用户b
     */
    String playerB;

    /**
     * 用户c
     */
    String playerC;

    /**
     * 用户d
     */
    String playerD;

    /**
     * 用户a分数
     */
    int scoreA;

    /**
     * 用户b分数
     */
    int scoreB;

    /**
     * 用户c分数
     */
    int scoreC;

    /**
     * 用户d分数
     */
    int scoreD;

    /**
     * 决斗开始时间
     */
    long startTime;

    /**
     * 决斗结束时间
     */
    long endTime;
}
