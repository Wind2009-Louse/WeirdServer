package com.weird.model.enums;

/**
 * 尘相关ENUM
 *
 * @author Nidhogg
 * @date 2020.9.20
 */
public enum DustEnum {
    /**
     * 获得NR
     */
    GET_NR(1, 1),

    /**
     * 获得闪
     */
    GET_RARE(2, 50),

    /**
     * 合成NR
     */
    TO_NR(3, 15),

    /**
     * 合成随机闪
     */
    TO_RANDOM(4, 150),

    /**
     * 合成指定闪
     */
    TO_ALTER(5, 300);

    private final int id;
    private final int count;

    DustEnum(int id, int count) {
        this.id = id;
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public int getCount() {
        return count;
    }
}
