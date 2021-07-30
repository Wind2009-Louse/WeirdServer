package com.weird.model.enums;

/**
 * 收藏操作
 *
 * @author Nidhogg
 * @date 2021.7.28
 */
public enum CollectionOperationEnum {
    /**
     * 添加
     */
    ADD(1, "添加"),
    /**
     * 移除
     */
    REMOVE(2, "移除");

    private final int id;
    private final String name;

    CollectionOperationEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static CollectionOperationEnum getById(int id) {
        for (CollectionOperationEnum data : CollectionOperationEnum.values()) {
            if (data.getId() == id) {
                return data;
            }
        }
        return null;
    }
}
