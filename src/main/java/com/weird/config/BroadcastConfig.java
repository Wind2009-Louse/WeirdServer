package com.weird.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 广播配置
 *
 * @author Nidhogg
 * @date 2021.11.22
 */
@ConfigurationProperties(prefix = "broadcast")
@Component
@Data
public class BroadcastConfig {
    private boolean enable;

    private String url;

    private String groupIdStr;

    private String groupForwardUrl;

    private String privateForwardUrl;

    private int retryTimes = 3;

    private int retrySecond = 5;
}
