package com.weird.model.enums;

/**
 * 怪兽属性
 *
 * @author Nidhogg
 * @date 2021.7.16
 */
public enum CardAttributeEnum {
    /**
     * 地
     */
    EARTH("地", 0x1),
    WATER("水", 0x2),
    FIRE("炎", 0x4),
    LIGHT("光", 0x10),
    DARK("暗", 0x20),
    GOD("神", 0x40);

    private final String name;
    private final int value;

    CardAttributeEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public static CardAttributeEnum getByName(String name) {
        for (CardAttributeEnum data : CardAttributeEnum.values()) {
            if (data.getName().equals(name)) {
                return data;
            }
        }
        return null;
    }
}
