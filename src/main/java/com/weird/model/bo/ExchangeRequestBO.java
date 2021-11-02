package com.weird.model.bo;

import com.alibaba.fastjson.JSONObject;
import com.weird.model.enums.ExchangeRequestEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import static com.weird.utils.BroadcastUtil.TIME_FORMAT;

/**
 * 交换请求
 *
 * @author Nidhogg
 * @date 2021.10.28
 */
@Data
public class ExchangeRequestBO implements Serializable {
    String hash;

    /**
     * 请求时间
     */
    long requestTime;

    /**
     * 消息发送者
     */
    String userName;

    /**
     * 请求对方
     */
    String targetName;

    /**
     * 己方卡片
     */
    String selfCardName;

    /**
     * 目标卡片
     */
    String targetCardName;

    /**
     * 当前状态
     */
    ExchangeRequestEnum status;

    /**
     * 请求消息
     */
    JSONObject request;

    public ExchangeRequestBO() {
        requestTime = System.currentTimeMillis();
        status = ExchangeRequestEnum.WAITING_TARGET;
        userName = "";
        targetName = "";
        selfCardName = "";
        targetCardName = "";
    }

    public ExchangeRequestBO(String userName, String targetName, String selfCardName, String targetCardName, JSONObject o) {
        requestTime = System.currentTimeMillis();
        status = ExchangeRequestEnum.WAITING_TARGET;
        this.userName = userName;
        this.targetName = targetName;
        this.selfCardName = selfCardName;
        this.targetCardName = targetCardName;
        this.request = o;
    }

    public String print() {
        return String.format("(%s)%s:%s的[%s]和%s的[%s]交换",
                hash,
                TIME_FORMAT.format(new Date(requestTime)),
                userName,
                selfCardName,
                targetName,
                targetCardName);
    }
}
