package com.weird.model.bo;

import com.alibaba.fastjson.JSONObject;
import com.weird.model.PackageInfoModel;
import com.weird.model.enums.RollRequestTypeEnum;
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
     * 是否展示全部结果
     */
    boolean showAll;

    /**
     * 抽卡数量
     */
    int rollCount;

    /**
     * 抽卡类型
     */
    RollRequestTypeEnum type;

    /**
     * 请求重抽的卡名
     */
    String reRollCardName;

    /**
     * 请求重抽的目标卡名
     */
    String reRollDescName;

    public RollRequestBO() {
        requestTime = System.currentTimeMillis();
        userName = "";
        packageInfo = new PackageInfoModel();
        rollCount = -1;
        rareToStop = false;
        showAll = false;
    }

    public RollRequestBO(String userName, PackageInfoModel packageInfo, int rollCount, JSONObject request) {
        this.requestTime = System.currentTimeMillis();
        this.userName = userName;
        this.packageInfo = packageInfo;
        this.rollCount = rollCount;
        this.request = request;
        this.rareToStop = false;
        this.showAll = false;
        this.type = RollRequestTypeEnum.NORMAL;
    }

    public RollRequestBO(String userName, PackageInfoModel packageInfo, int rollCount, JSONObject request, RollRequestTypeEnum type) {
        this.requestTime = System.currentTimeMillis();
        this.userName = userName;
        this.packageInfo = packageInfo;
        this.rollCount = rollCount;
        this.request = request;
        this.rareToStop = false;
        this.showAll = false;
        this.type = type;
    }
}
