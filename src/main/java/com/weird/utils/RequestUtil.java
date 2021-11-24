package com.weird.utils;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 接口处理工具包
 *
 * @author Nidhogg
 * @date 2021.11.25
 */
public class RequestUtil {
    public static JSONObject getJsonFromRequest(HttpServletRequest request) throws IOException {
        if (request == null) {
            return new JSONObject();
        }
        JSONObject o;
        if (!request.getParameterMap().isEmpty()) {
            Map<String, String[]> parameterMap = request.getParameterMap();
            o = new JSONObject();
            parameterMap.forEach((k, v) -> o.put(k, v[0]));
        } else {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String appendStr;
            while ((appendStr = bufferedReader.readLine()) != null) {
                sb.append(appendStr);
            }
            String str = sb.toString();
            o = JSONObject.parseObject(str);
        }
        return o;
    }
}
