package com.weird.model.enums;

/**
 * 冒险描述
 *
 * @author Nidhogg
 * @date 2021.10.18
 */
public class AdventureEnumConst {
    public static final String NORMAL_DESC = "胜利DP+16，失败DP+8";
    public static final String PER_ROUND = "\n经过了一圈地图，DP额外+25！";

    public static final String SCORPION = "遭遇黑蝎盗掘团！\n胜利：DP+0，获得道具[万宝槌](将一张已有闪卡随机替换为同包其它闪卡)\n失败：DP-25";

    public static final String FIELD_CROSS_WAY = "\n地图：命运的分岔道\n* 无论胜负，DP+1";
    public static final String FIELD_WASTELAND = "\n地图：纷争的荒野\n* 胜利DP+2";
    public static final String FIELD_GRAVE = "\n地图：暗黑的墓场\n* 败北DP+2";
    public static final String FIELD_FOREST = "\n地图：神圣森林\n* 胜利DP+3，败北DP-3";

    public static final String FIELD_JUSTICE = "\n地图：正义的同伴\n* 无论胜负，DP+9";
    public static final String FIELD_CELESTIA = "\n地图：异层空间\n* 胜利DP+20，失败DP+6\n概率前往[死域海的灯塔]\n* 胜利DP+32，失败DP+16，胜利更可额外获得奖品卡[神之摄理]！";
    public static final String FIELD_SEA = "\n地图：燃起的大海\n* 胜利DP+18，失败DP+10";

    public static final int ADVENTURE_ROUND = 24;
}
