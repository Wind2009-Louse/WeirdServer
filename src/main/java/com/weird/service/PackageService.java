package com.weird.service;

/**
 * 卡包Service
 * @author Nidhogg
 */
public interface PackageService {
    /**
     * 添加卡包
     *
     * @param name 卡包名称
     * @return 是否添加成功
     */
    boolean addPackage(String name);

    /**
     * 更新卡包名称
     *
     * @param oldName 旧卡包名称
     * @param newName 新卡包名称
     * @return 是否更新成功
     */
    boolean updatePackageName(String oldName, String newName);
}
