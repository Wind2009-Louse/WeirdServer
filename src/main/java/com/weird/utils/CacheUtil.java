package com.weird.utils;

import com.weird.model.dto.CardOwnListDTO;
import com.weird.model.dto.RollListDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 缓存工具
 *
 * @author Nidhogg
 * @date 2020.9.25
 */
@Slf4j
public class CacheUtil {
    /**
     * 卡片详情缓存
     */
    public static Map<String, String> DetailCache = new HashMap<>();

    /**
     * 卡片列表缓存，避免频繁联表查询
     */
    public static Map<String, List<CardOwnListDTO>> cardListCache = new HashMap<>();

    /**
     * 抽卡记录列表缓存，避免频繁查全表
     */
    public static Map<String, PageResult<RollListDTO>> rollListCache = new HashMap<>();

    /**
     * 更新数据后，手动清除卡片列表缓存
     */
    public static void clearCardListCache() {
        log.debug("卡片列表数据缓存被清除");
        cardListCache.clear();
    }

    /**
     * 更新数据后，手动清除抽卡记录列表缓存
     */
    public static void clearRollListCache() {
        log.debug("抽卡记录列表缓存被清除");
        rollListCache.clear();
    }
}
