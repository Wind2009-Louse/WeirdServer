package com.weird.facade;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * 通过http接口发送QQ信息
 *
 * @author Nidhogg
 * @date 2021.9.24
 */
@Component
@Slf4j
public class BroadcastFacade {
    @Value("${broadcast.enable}")
    private boolean enable;

    /**
     * HTTP接口URL
     */
    @Value("${broadcast.url}")
    private String APIUrl;

    /**
     * 发送群ID
     */
    @Value("${broadcast.group}")
    private String groupID;

    @Value("${broadcast.retry.times:3}")
    private int retryTimes;

    @Value("${broadcast.retry.second:5}")
    private int retrySecond;

    private final static String HEADER_JSON = "application/json";

    public void sendMsgAsync(String msg, int sleepMill) {
        Runnable runnable = () -> sendMsg(msg, sleepMill);
        CompletableFuture.runAsync(runnable);
    }

    public void sendMsgAsync(String msg) {
        sendMsgAsync(msg, 0);
    }

    public void sendMsg(String msg, int sleepMill) {
        if (!enable) {
            return;
        }
        if (StringUtils.isEmpty(APIUrl) || StringUtils.isEmpty(groupID)) {
            log.error("未配置广播信息！");
            return;
        }

        int retryTimes = this.retryTimes;
        try {
            if (sleepMill > 0) {
                Thread.sleep(sleepMill);
            }
        } catch (InterruptedException e) {
        }
        while (retryTimes >= 0) {
            JSONObject jsonObject;
            if (retryTimes < this.retryTimes) {
                jsonObject = sendMsg("(R)" + msg);
            } else {
                jsonObject = sendMsg(msg);
            }
            if (!"ok".equals(jsonObject.get("status"))) {
                log.warn("发送消息失败：{}。提示信息：{}", msg, jsonObject.toJSONString());
                retryTimes --;
            } else {
                break;
            }
            if (retryTimes >= 0) {
                try {
                    if (sleepMill > 0) {
                        Thread.sleep(retrySecond);
                    }
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public JSONObject sendMsg(String msg) {
        String[] groupList = groupID.split(",");
        for (String id : groupList) {
            if (StringUtils.isEmpty(id)) {
                continue;
            }
            CloseableHttpClient httpClient = null;
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("group_id", id);
                jsonObject.put("message", "【广播】" + msg);

                HttpPost postRequest = new HttpPost(APIUrl);
                postRequest.addHeader(HTTP.CONTENT_TYPE, HEADER_JSON);
                StringEntity param = new StringEntity(jsonObject.toJSONString(), StandardCharsets.UTF_8);
                param.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, HEADER_JSON));
                postRequest.setEntity(param);

                httpClient = HttpClientBuilder.create().build();
                CloseableHttpResponse result = httpClient.execute(postRequest);
                String resultString = EntityUtils.toString(result.getEntity());
                return JSON.parseObject(resultString);
            } catch (Exception e) {
                log.error("发送广播失败：{}", e.getMessage());
            } finally {
                if (httpClient != null) {
                    try {
                        httpClient.close();
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }
            }
        }
        return null;
    }

}