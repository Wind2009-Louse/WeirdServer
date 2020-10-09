package com.weird.utils;

import com.weird.model.dto.CardOwnListDTO;
import com.weird.model.dto.RollListDTO;
import lombok.extern.slf4j.Slf4j;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

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
     * 卡片持有列表缓存，避免频繁联表查询
     */
    public static ExpiringMap<String, List<CardOwnListDTO>> cardListCache = ExpiringMap.builder()
            .maxSize(200)
            .expirationPolicy(ExpirationPolicy.ACCESSED).build();

    public static List<CardOwnListDTO> getCardOwnListCache(String key) {
        return cardListCache.get(key);
    }

    public static void putCardOwnListCache(String key, List<CardOwnListDTO> value) {
        cardListCache.put(key, value);
    }

    public static void clearCardOwnListCache() {
        log.debug("卡片列表数据缓存被清除");
        cardListCache.clear();
    }

    /**
     * 抽卡详细记录列表缓存，避免频繁查两次全表
     */
    public static ExpiringMap<String, PageResult<RollListDTO>> rollListWithDetailCache = ExpiringMap.builder()
            .maxSize(200)
            .expirationPolicy(ExpirationPolicy.ACCESSED).build();

    public static PageResult<RollListDTO> getRollListWithDetailCache(String key) {
        return rollListWithDetailCache.get(key);
    }

    public static void putRollListWithDetailCache(String key, PageResult<RollListDTO> value) {
        rollListWithDetailCache.put(key, value);
    }

    public static void clearRollListWithDetailCache() {
        log.debug("抽卡记录列表缓存被清除");
        rollListWithDetailCache.clear();
        rollListCache.clear();
    }

    /**
     * 抽卡记录列表缓存，避免频繁查全表
     */
    public static Map<String, List<RollListDTO>> rollListCache = ExpiringMap.builder()
            .maxSize(200)
            .expirationPolicy(ExpirationPolicy.ACCESSED).build();

    public static List<RollListDTO> getRollListCache(String key) {
        return rollListCache.get(key);
    }

    public static void putRollListCache(String key, List<RollListDTO> value) {
        rollListCache.put(key, value);
    }
}
