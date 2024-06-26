package com.weird.model.bo;

import com.alibaba.fastjson.JSONObject;
import com.weird.model.PackageInfoModel;
import com.weird.model.enums.RollRequestTypeEnum;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;

import static com.weird.utils.BroadcastUtil.TIME_FORMAT;

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

    /**
     * 请求时用户的DP
     */
    int originDp;

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

    public String print() {
        String result = "";
        switch (type) {
            case NORMAL:
                result = String.format("%s:%s抽%d包[%s]",
                        TIME_FORMAT.format(new Date(requestTime)),
                        userName,
                        rollCount,
                        packageInfo.getPackageName());
                if (rareToStop) {
                    result += "，闪停";
                }
                if (showAll) {
                    result += "(全)";
                }
                return result;
            case REROLL:
                return String.format("%s:%s重抽[%s]的[%s]",
                        TIME_FORMAT.format(new Date(requestTime)),
                        userName,
                        packageInfo.getPackageName(),
                        reRollCardName);
            case LEGEND:
                result = String.format("%s:%s抽传说卡",
                        TIME_FORMAT.format(new Date(requestTime)),
                        userName);
                if (!StringUtils.isEmpty(reRollCardName)) {
                    result += "(当前为[" + reRollCardName + "])";
                }
                return result;
            case LEGEND_CONFIRM:
                return String.format("%s:%s将[%s]替换为[%s]",
                        TIME_FORMAT.format(new Date(requestTime)),
                        userName,
                        reRollCardName,
                        reRollDescName);
            default:
                break;
        }
        return result;
    }
}
