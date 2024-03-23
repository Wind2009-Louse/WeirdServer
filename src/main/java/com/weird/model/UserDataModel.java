package com.weird.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户信息
 *
 * @author Nidhogg
 * @date 2020/09/04
 */
@Data
public class UserDataModel implements Serializable {
    /**
     * 用户ID
     */
    private int userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 关联QQ号
     */
    private String qq;

    /**
     * 是否管理员
     */
    private Byte isAdmin;

    /**
     * 未出货的计数
     */
    private int nonawardCount;

    /**
     * 尘数
     */
    private int dustCount;

    /**
     * DP
     */
    private int duelPoint;

    /**
     * 硬币
     */
    private int coin;

    /**
     * 当天胜利次数
     */
    private int dailyWin;

    /**
     * 当天失败次数
     */
    private int dailyLost;

    /**
     * 当天是否出货
     */
    private int dailyAward;

    /**
     * 当天抽卡次数
     */
    private int dailyRoll;

    /**
     * 每周换NR的次数
     */
    private int weeklyDustChangeN;

    /**
     * 每周换随机闪的次数
     */
    private int weeklyDustChangeR;

    /**
     * 每周换自选闪的次数
     */
    private int weeklyDustChangeAlter;

    /**
     * 每周完成任务的次数
     */
    private int weeklyMission;

    /**
     * 转盘次数
     */
    private int roulette;

    /**
     * 抽卡次数
     */
    private int rollCount;

    /**
     * 百八次数
     */
    private int doubleRareCount;
}