package com.weird.model.enums;

/**
 * 抽卡请求类型
 *
 * @author Nidhogg
 * @date 2021.10.26
 */
public enum RollRequestTypeEnum {
    /**
     * 普通抽卡
     */
    NORMAL(1, "抽卡"),
    /**
     * 重抽
     */
    REROLL(2, "万宝槌"),
    /**
     * 抽传说
     */
    LEGEND(3, "传说卡"),
    /**
     * 更换传说
     */
    LEGEND_CONFIRM(4, "传说卡更换确认");

    private final int id;
    private final String name;

    RollRequestTypeEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static String getById(int id) {
        for (RollRequestTypeEnum value : RollRequestTypeEnum.values()) {
            if (value.getId() == id) {
                return value.name;
            }
        }
        return null;
    }
}
