package com.weird.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 自定义效果配置
 *
 * @author Nidhogg
 * @date 2021.12.01
 */
@ConfigurationProperties(prefix = "altereffect")
@Component
@Data
public class AlterEffectConfig {
    static List<List<String>> list = null;
    public void setList(List<List<String>> newList) {
        list = newList;
    }
    static public List<List<String>> getList() {
        return list;
    }
}
