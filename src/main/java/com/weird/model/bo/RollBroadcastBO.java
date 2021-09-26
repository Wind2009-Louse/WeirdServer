package com.weird.model.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 抽卡统计
 *
 * @author Nidhogg
 * @date 2021.9.26
 */
@Data
public class RollBroadcastBO implements Serializable {
    /**
     * 统计对象
     */
    String name;

    /**
     * 总数量
     */
    long totalCount;

    /**
     * 闪数量
     */
    long rareCount;

    public RollBroadcastBO() {
        name = "";
    }

    public RollBroadcastBO(String name) {
        this.name = name;
    }

    public double getRareRate() {
        return (double) rareCount * 100.00 / (double) totalCount;
    }
}
