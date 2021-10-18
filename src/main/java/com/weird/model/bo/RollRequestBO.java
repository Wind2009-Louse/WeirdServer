package com.weird.model.bo;

import com.alibaba.fastjson.JSONObject;
import com.weird.model.PackageInfoModel;
import lombok.Data;

import java.io.Serializable;

/**
 * 抽卡请求
 *
 * @author Nidhogg
 * @date 2021.10.15
 */
@Data
public class RollRequestBO implements Serializable {
    /**
     * 请求时间
     */
    long requestTime;

    /**
     * 消息发送者
     */
    String userName;

    /**
     * 卡包内容
     */
    PackageInfoModel packageInfo;

    /**
     * 发起的请求
     */
    JSONObject request;

    /**
     * 是否闪停
     */
    boolean rareToStop;

    /**
     * 是否百八
     */
    boolean doubleRare;

    /**
     * 抽卡数量
     */
    int rollCount;

    public RollRequestBO() {
        requestTime = System.currentTimeMillis();
        userName = "";
        packageInfo = new PackageInfoModel();
        rollCount = -1;
        rareToStop = false;
        doubleRare = false;
    }

    public RollRequestBO(String userName, PackageInfoModel packageInfo, int rollCount, JSONObject request) {
        this.requestTime = System.currentTimeMillis();
        this.userName = userName;
        this.packageInfo = packageInfo;
        this.rollCount = rollCount;
        this.request = request;
        this.rareToStop = false;
        this.doubleRare = false;
    }
}
