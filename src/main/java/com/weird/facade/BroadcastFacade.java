package com.weird.facade;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.weird.config.BroadcastConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    @Autowired
    BroadcastConfig broadcastConfig;

    private final static String HEADER_JSON = "application/json";

    final static JSONObject EMPTY_JSON = new JSONObject();

    public void sendMsgAsync(String msg, int sleepMill) {
        Runnable runnable = () -> sendMsg(msg, broadcastConfig.getUrl(), sleepMill);
        CompletableFuture.runAsync(runnable);
    }

    public void sendMsgAsync(String msg) {
        sendMsgAsync(msg, 0);
    }

    public void sendMsgAsync(JSONObject sendObject, String url, int sleepMill) {
        Runnable runnable = () -> sendMsg(sendObject, url, sleepMill);
        CompletableFuture.runAsync(runnable);
    }

    public void sendMsgAsync(JSONObject sendObject) {
        sendMsgAsync(sendObject, broadcastConfig.getUrl(),0);
    }

    public void sendGroupForwardMsgAsync(JSONObject sendObject) {
        sendMsgAsync(sendObject, broadcastConfig.getGroupForwardUrl(), 0);
    }

    public void sendGroupForwardMsgAsync(List<String> msgList) {
        String groupIdStr = broadcastConfig.getGroupIdStr();
        if (StringUtils.isEmpty(groupIdStr)) {
            log.error("未配置广播信息！");
            return;
        }

        String[] groupList = groupIdStr.split(",");
        for (String id : groupList) {
            if (StringUtils.isEmpty(id)) {
                continue;
            }

            List<JSONObject> chatData = new ArrayList<>();
            for (String msg : msgList) {
                // 创建最内层的 "data" 对象
                JSONObject textData = new JSONObject();
                textData.put("text", msg);

                // 创建包含 "type" 和 "data" 的内部对象
                JSONObject contentData = new JSONObject();
                contentData.put("type", "text");
                contentData.put("data", textData);

                chatData.add(contentData);
            }
            JSONObject nodeData = new JSONObject();
            nodeData.put("content", chatData);

            // 创建最终的 "messages" 数组
            JSONObject node = new JSONObject();
            node.put("type", "node");
            node.put("data", nodeData);

            JSONObject response = new JSONObject();
            response.put("messages", Collections.singletonList(node));
            response.put(GROUP_ID, id);

            sendMsgAsync(response, broadcastConfig.getGroupForwardUrl(), 0);
        }
    }

    public void sendPrivateForwardMsgAsync(JSONObject sendObject) {
        sendMsgAsync(sendObject, broadcastConfig.getPrivateForwardUrl(), 0);
    }

    /**
     * 发送到广播群组
     *
     * @param msg       发送信息
     * @param sleepMill 延迟时间
     */
    private void sendMsg(String msg, String url, int sleepMill) {
        if (!broadcastConfig.isEnable()) {
            return;
        }
        String groupIdStr = broadcastConfig.getGroupIdStr();
        if (StringUtils.isEmpty(url) || StringUtils.isEmpty(groupIdStr)) {
            log.error("未配置广播信息！");
            return;
        }

        // 延迟
        int retryTimes = broadcastConfig.getRetryTimes();
        try {
            if (sleepMill > 0) {
                Thread.sleep(sleepMill);
            }
        } catch (InterruptedException e) {
        }

        String[] groupList = groupIdStr.split(",");
        for (String id : groupList) {
            if (StringUtils.isEmpty(id)) {
                continue;
            }
            JSONObject sendObject = new JSONObject();
            sendObject.put(GROUP_ID, id);
            sendObject.put("message", msg);
            while (retryTimes >= 0) {
                if (retryTimes < broadcastConfig.getRetryTimes()) {
                    sendObject.put("message", "(R)" + msg);
                }
                JSONObject responseObject = sendMsg(sendObject, url);
                if (!"ok".equals(responseObject.get("status"))) {
                    log.warn("发送消息失败：{}。提示信息：{}", msg, responseObject.toJSONString());
                    retryTimes--;
                } else {
                    break;
                }
                if (retryTimes >= 0) {
                    try {
                        if (sleepMill > 0) {
                            Thread.sleep(broadcastConfig.getRetrySecond());
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
    private void sendMsg(JSONObject sendObject, String url, int sleepMill) {
        if (!broadcastConfig.isEnable() || sendObject == null) {
            return;
        }
        String groupIdStr = broadcastConfig.getGroupIdStr();
        if (StringUtils.isEmpty(url) || StringUtils.isEmpty(groupIdStr)) {
            log.error("未配置广播信息！");
            return;
        }
        String msg = sendObject.getString("message");

//        if ("private".equals(sendObject.getString("message_type"))) {
//            return;
//        }

        // 延迟
        int retryTimes = broadcastConfig.getRetryTimes();
        try {
            if (sleepMill > 0) {
                Thread.sleep(sleepMill);
            }
        } catch (InterruptedException e) {
        }

        while (retryTimes >= 0) {
            if (retryTimes < broadcastConfig.getRetryTimes() && !StringUtils.isEmpty(msg)) {
                sendObject.put("message", "(R)" + msg);
            }
            JSONObject responseObject = sendMsg(sendObject, url);
            if (!"ok".equals(responseObject.getOrDefault("status", ""))) {
                log.warn("发送消息失败：{}。提示信息：{}", msg, responseObject.toJSONString());
                retryTimes--;
            } else {
                break;
            }
            if (retryTimes >= 0) {
                try {
                    if (sleepMill > 0) {
                        Thread.sleep(broadcastConfig.getRetrySecond());
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
    private JSONObject sendMsg(JSONObject jsonObject, String url) {
        CloseableHttpClient httpClient = null;
        try {
            HttpPost postRequest = new HttpPost(url);
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
