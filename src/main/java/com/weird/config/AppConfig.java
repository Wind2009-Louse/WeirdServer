package com.weird.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

@Configuration
@EnableWebMvc
@ComponentScan(value = "com.weird", includeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {org.springframework.stereotype.Controller.class})
})
public class AppConfig implements WebMvcConfigurer {
    @Bean
    public HttpMessageConverter<String> responseBodyConverter() {
        StringHttpMessageConverter converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        return converter;
    }

    /**
     * 配置消息转换器--这里我用的是alibaba 开源的 fastjson
     *
     * @param converters
     * @author fxbin
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 1.需要定义一个convert转换消息的对象;
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        // 2.添加fastJson的配置信息，比如：是否要格式化返回的json数据;
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteDateUseDateFormat);
        // 3.处理中文乱码问题
        List<MediaType> fastMediaTypes = new LinkedList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        // 4.在convert中添加配置信息.
        fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        // 5.将convert添加到converters当中.
        converters.add(fastJsonHttpMessageConverter);
    }
}