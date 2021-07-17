package com.weird.model.enums;

/**
 * 抽卡结果
 *
 * @author Nidhogg
 * @date 2021.7.17
 */
public enum RollStatusEnum {
    /**
     * 生效
     */
    VALID(0, "生效"),
    /**
     * 失效
     */
    INVALID(1, "失效");


    private final int id;
    private final String name;

    RollStatusEnum(int id, String name) {
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
        for (RollStatusEnum value : RollStatusEnum.values()) {
            if (value.getId() == id) {
                return value.name;
            }
        }
        return null;
    }
}
