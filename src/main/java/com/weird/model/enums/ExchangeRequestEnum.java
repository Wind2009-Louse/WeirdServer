package com.weird.model.enums;

/**
 * 交换请求类型
 *
 * @author Nidhogg
 * @date 2021.10.26
 */
public enum ExchangeRequestEnum {
    /**
     * 等待对方同意
     */
    WAITING_TARGET(1, "等待对方同意"),
    /**
     * 等待管理员同意
     */
    WAITING_ADMIN(2, "等待管理员同意"),

    /**
     * 同意请求
     */
    AGREE(11, "同意"),

    /**
     * 拒绝请求
     */
    REJECT(12, "拒绝");

    private final int id;
    private final String name;

    ExchangeRequestEnum(int id, String name) {
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
        for (ExchangeRequestEnum value : ExchangeRequestEnum.values()) {
            if (value.getId() == id) {
                return value.name;
            }
        }
        return null;
    }
}
