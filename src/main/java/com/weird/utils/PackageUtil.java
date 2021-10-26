package com.weird.utils;

import com.weird.model.dto.CardListDTO;

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

    static public int NORMAL_RARE_RATE = 4;

    static public int DOUBLE_RARE_RATE = 8;

    /**
     * 判断该卡包是否可以由玩家抽取
     *
     * @param packageName 卡包名
     * @return 结果
     */
    static public boolean canNotRoll(String packageName) {
        return packageName.contains("[SP]") || packageName.contains("[sp]");
    }

    /**
     * 判断该卡包是否传说卡
     *
     * @param packageName 卡包名
     * @return 结果
     */
    static public boolean isLegendPackage(String packageName) {
        return canNotRoll(packageName) && (packageName.contains("legend") || packageName.contains("LEGEND"));
    }

    /**
     * 输出卡片的卡包名和卡片名
     *
     * @param card 卡片信息
     * @return 字符串信息
     */
    static public String printCard(CardListDTO card) {
        if (PackageUtil.NR_LIST.contains(card.getRare())) {
            return String.format("[%s]%s", card.getRare(), card.getCardName());
        } else {
            return String.format("【%s】%s", card.getRare(), card.getCardName());
        }
    }
}
