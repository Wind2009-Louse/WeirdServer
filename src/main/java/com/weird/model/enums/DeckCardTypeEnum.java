package com.weird.model.enums;

/**
 * 卡组卡片类型
 *
 * @author Nidhogg
 * @date 2021.7.28
 */
public enum DeckCardTypeEnum {
    /**
     * 主卡组
     */
    MAIN(1, "main"),
    /**
     * 额外卡组
     */
    EX(2, "ex"),
    /**
     * 副卡组
     */
    SIDE(3, "side");

    private final int id;
    private final String name;

    DeckCardTypeEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static DeckCardTypeEnum getById(int id) {
        for (DeckCardTypeEnum data : DeckCardTypeEnum.values()) {
            if (data.getId() == id) {
                return data;
            }
        }
        return null;
    }
}
