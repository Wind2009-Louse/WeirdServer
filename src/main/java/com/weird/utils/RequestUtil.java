package com.weird.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 接口处理工具包
 *
 * @author Nidhogg
 * @date 2021.11.25
 */
@Slf4j
public class RequestUtil {
    public static JSONObject getJsonFromRequest(HttpServletRequest request) throws Exception {
        if (request == null) {
            return new JSONObject();
        }
        JSONObject o;
        if (!request.getParameterMap().isEmpty()) {
            Map<String, String[]> parameterMap = request.getParameterMap();
            o = new JSONObject();
            parameterMap.forEach((k, v) -> o.put(k, v[0]));
            Map<String, MultipartFile> fileMap = ((StandardMultipartHttpServletRequest) request).getFileMap();
            fileMap.forEach((k, v) -> {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(v.getInputStream(), StandardCharsets.UTF_8));
                    StringBuilder sb = new StringBuilder();
                    String appendStr;
                    while ((appendStr = bufferedReader.readLine()) != null) {
                        sb.append(appendStr).append("\n");
                    }
                    String str = sb.toString();
                    o.put(k, str);
                } catch (Exception e) {
                    log.error("解析文件失败：", e);
                }
            });
        } else {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String appendStr;
            while ((appendStr = bufferedReader.readLine()) != null) {
                sb.append(appendStr).append("\n");
            }
            String str = sb.toString();
            o = JSONObject.parseObject(str);
        }
        return o;
    }
}
