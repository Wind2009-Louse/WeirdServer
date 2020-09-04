package com.weird.model;

import lombok.Data;

/**
 * 卡包信息
 *
 * @author Nidhogg
 * @date   2020/09/04
 */
@Data
public class PackageInfoModel {
    /**
     * 卡包ID
     */
    private int packageId;

    /**
     * 卡包名称
     */
    private String packageName;
}