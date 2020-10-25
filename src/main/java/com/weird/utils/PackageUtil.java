package com.weird.utils;

import java.util.Arrays;
import java.util.List;

/**
 * 卡包处理
 *
 * @author Nidhogg
 * @date 2020.9.29
 */
public class PackageUtil {
    static public List<String> RARE_LIST = Arrays.asList("N", "R", "SR", "UR", "HR", "GR", "SER");

    static public List<String> NR_LIST = Arrays.asList("N", "R");

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
