package com.weird.utils;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字符串拓展功能
 *
 * @author Nidhogg
 * @date 2021.10.15
 */
public class StringExtendUtil {
    public static List<String> split(String origin, String regex) {
        if (StringUtils.isEmpty(origin)) {
            return Collections.emptyList();
        }
        List<String> originList = Arrays.asList(origin.split(regex));
        return originList.stream().filter(s -> !s.isEmpty()).collect(Collectors.toList());
    }
}
