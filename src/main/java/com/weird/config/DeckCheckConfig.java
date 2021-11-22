package com.weird.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

/**
 * 卡组检查配置
 *
 * @author Nidhogg
 * @date 2021.11.22
 */
@ConfigurationProperties(prefix = "deckcheck")
@Component
@Data
public class DeckCheckConfig {
    Map<String, Map<String, Object>> arg = Collections.emptyMap();
}
