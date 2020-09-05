package com.weird.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 卡包信息
 *
 * @author Nidhogg
 * @date   2020/09/04
 */
@Data
public class PackageInfoModel implements Serializable {
    /**
     * 卡包ID
     */
    private int packageId;

    /**
     * 卡包名称
     */
    private String packageName;
}