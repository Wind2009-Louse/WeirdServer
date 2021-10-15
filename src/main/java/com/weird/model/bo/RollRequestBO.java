package com.weird.model.bo;

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
     * 抽卡数量
     */
    int rollCount;

    public RollRequestBO() {
        requestTime = System.currentTimeMillis();
        userName = "";
        packageInfo = new PackageInfoModel();
        rollCount = -1;
    }

    public RollRequestBO(String userName, PackageInfoModel packageInfo, int rollCount) {
        this.requestTime = System.currentTimeMillis();
        this.userName = userName;
        this.packageInfo = packageInfo;
        this.rollCount = rollCount;
    }
}
