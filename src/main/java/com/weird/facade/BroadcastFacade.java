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

import static com.weird.utils.BroadcastUtil.GROUP_ID;

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

    final static JSONObject EMPTY_JSON = new JSONObject();

    public void sendMsgAsync(String msg, int sleepMill) {
        Runnable runnable = () -> sendMsg(msg, sleepMill);
        CompletableFuture.runAsync(runnable);
    }

    public void sendMsgAsync(String msg) {
        sendMsgAsync(msg, 0);
    }

    public void sendMsgAsync(JSONObject sendObject, int sleepMill) {
        Runnable runnable = () -> sendMsg(sendObject, sleepMill);
        CompletableFuture.runAsync(runnable);
    }

    public void sendMsgAsync(JSONObject sendObject) {
        sendMsgAsync(sendObject, 0);
    }

    /**
     * 发送到广播群组
     *
     * @param msg       发送信息
     * @param sleepMill 延迟时间
     */
    private void sendMsg(String msg, int sleepMill) {
        if (!enable) {
            return;
        }
        if (StringUtils.isEmpty(APIUrl) || StringUtils.isEmpty(groupID)) {
            log.error("未配置广播信息！");
            return;
        }

        // 延迟
        int retryTimes = this.retryTimes;
        try {
            if (sleepMill > 0) {
                Thread.sleep(sleepMill);
            }
        } catch (InterruptedException e) {
        }

        String[] groupList = groupID.split(",");
        for (String id : groupList) {
            if (StringUtils.isEmpty(id)) {
                continue;
            }
            JSONObject sendObject = new JSONObject();
            sendObject.put(GROUP_ID, id);
            sendObject.put("message", msg);
            while (retryTimes >= 0) {
                if (retryTimes < this.retryTimes) {
                    sendObject.put("message", "(R)" + msg);
                }
                JSONObject responseObject = sendMsg(sendObject);
                if (!"ok".equals(responseObject.get("status"))) {
                    log.warn("发送消息失败：{}。提示信息：{}", msg, responseObject.toJSONString());
                    retryTimes--;
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
    }

    /**
     * 发送指定信息
     *
     * @param sendObject 发送内容
     * @param sleepMill  延迟时间
     */
    private void sendMsg(JSONObject sendObject, int sleepMill) {
        if (!enable || sendObject == null) {
            return;
        }
        if (StringUtils.isEmpty(APIUrl) || StringUtils.isEmpty(groupID)) {
            log.error("未配置广播信息！");
            return;
        }
        String msg = sendObject.getString("message");

        // 延迟
        int retryTimes = this.retryTimes;
        try {
            if (sleepMill > 0) {
                Thread.sleep(sleepMill);
            }
        } catch (InterruptedException e) {
        }

        while (retryTimes >= 0) {
            if (retryTimes < this.retryTimes) {
                sendObject.put("message", "(R)" + msg);
            }
            JSONObject responseObject = sendMsg(sendObject);
            if (!"ok".equals(responseObject.getOrDefault("status", ""))) {
                log.warn("发送消息失败：{}。提示信息：{}", msg, responseObject.toJSONString());
                retryTimes--;
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

    /**
     * 实际发送消息的方法
     *
     * @param jsonObject 发送内容
     * @return 返回结果
     */
    private JSONObject sendMsg(JSONObject jsonObject) {
        CloseableHttpClient httpClient = null;
        try {
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
            log.error("发送信息失败：{}", e.getMessage());
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
        return EMPTY_JSON;
    }
}
