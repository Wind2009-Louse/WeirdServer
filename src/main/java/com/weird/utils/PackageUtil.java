package com.weird.utils;

/**
 * 卡包处理
 *
 * @author Nidhogg
 * @date 2020.9.29
 */
public class PackageUtil {
    /**
     * 判断该卡包是否可以由玩家抽取
     *
     * @param packageName 卡包名
     * @return 结果
     */
    static public boolean canNotRoll(String packageName) {
        return packageName.contains("[SP]") || packageName.contains("[sp]");
    }
}
