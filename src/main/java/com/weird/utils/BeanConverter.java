package com.weird.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean转换类
 */
public class BeanConverter {
    /**
     * 转换为新的Bean类
     *
     * @param source      Bean源
     * @param targetClass 转换Bean类
     * @return 转换结果
     */
    public static <K, T> K convert(T source, Class<K> targetClass) {
        K target = null;
        if (source != null) {
            try {
                // 初始化bean
                target = targetClass.newInstance();

                // 简单的直接拷贝
                BeanUtils.copyProperties(source, target);
            } catch (BeansException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return target;
    }

    /**
     * 转换为新的Bean类列表
     *
     * @param sources     Bean源
     * @param targetClass 转换Bean类
     * @return 转换结果
     */
    public static <K, T> List<K> convertList(List<T> sources, Class<K> targetClass) {
        List<K> targets = null;
        if (sources != null) {
            targets = new ArrayList<K>(sources.size());
            // 循环转换
            K target = null;
            for (T source : sources) {
                target = convert(source, targetClass);
                if (target != null) {
                    targets.add(target);
                }
            }
        }
        return targets;
    }
}
