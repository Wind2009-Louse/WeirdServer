package com.weird.utils;

import com.alibaba.druid.util.StringUtils;
import com.weird.model.dto.CardOwnListDTO;
import com.weird.model.dto.RollListDTO;
import com.weird.model.dto.UserSessionDTO;
import lombok.extern.slf4j.Slf4j;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 缓存工具
 *
 * @author Nidhogg
 * @date 2020.9.25
 */
@Slf4j
public class CacheUtil {
    /**
     * 卡片预览缓存
     */
    public static Map<String, String> PreviewCache = new HashMap<>();

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
    public static ExpiringMap<String, List<RollListDTO>> rollListCache = ExpiringMap.builder()
            .maxSize(200)
            .expirationPolicy(ExpirationPolicy.ACCESSED).build();

    public static List<RollListDTO> getRollListCache(String key) {
        return rollListCache.get(key);
    }

    public static void putRollListCache(String key, List<RollListDTO> value) {
        rollListCache.put(key, value);
    }


    /**
     * 令牌缓存
     */
    public static ExpiringMap<String, UserSessionDTO> userSessionCache = ExpiringMap.builder()
            .maxSize(200)
            .expiration(1, TimeUnit.DAYS)
            .expirationPolicy(ExpirationPolicy.ACCESSED).build();

    public static long UserSessionExpireTime = 1000 * 60 * 60 * 24;

    public static UserSessionDTO getUserSessionCache(String key) {
        UserSessionDTO value = userSessionCache.get(key);
        if (value != null) {
            value.setExpireTimestamp(System.currentTimeMillis() + UserSessionExpireTime);
            userSessionCache.remove(key);
            userSessionCache.put(key, value);
        }
        return value;
    }

    public static void putUserSessionCache(UserSessionDTO session) {
        // 清空旧缓存
        String oldSession = null;
        for (Map.Entry<String, UserSessionDTO> cacheData : userSessionCache.entrySet()) {
            UserSessionDTO oldData = cacheData.getValue();
            if (oldData == null) {
                continue;
            }
            if (oldData.getUserName().equals(session.getUserName())) {
                oldSession = cacheData.getKey();
            }
        }
        if (!StringUtils.isEmpty(oldSession)) {
            userSessionCache.remove(oldSession);
        }

        // 添加新缓存
        userSessionCache.put(session.getSession(), session);
    }
}
