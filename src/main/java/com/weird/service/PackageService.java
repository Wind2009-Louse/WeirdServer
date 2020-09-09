package com.weird.service;

import com.weird.model.PackageInfoModel;

import java.util.List;

/**
 * 卡包Service
 *
 * @author Nidhogg
 */
public interface PackageService {
    /**
     * 根据名称查找卡包列表
     *
     * @param packageName 卡包名
     * @return 卡包列表
     */
    List<PackageInfoModel> selectByName(String packageName) throws Exception;

    /**
     * 添加卡包
     *
     * @param name 卡包名称
     * @return 是否添加成功
     */
    boolean addPackage(String name) throws Exception;

    /**
     * 更新卡包名称
     *
     * @param oldName 旧卡包名称
     * @param newName 新卡包名称
     * @return 是否更新成功
     */
    boolean updatePackageName(String oldName, String newName) throws Exception;

    /**
     * 在卡包中添加卡片
     *
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param rare        稀有度
     * @return 是否添加成功
     */
    boolean addCard(String packageName, String cardName, String rare) throws Exception;

    /**
     * 修改卡包中的卡片名字
     *
     * @param packageName 卡包名
     * @param oldName     旧卡名
     * @param newName     新卡名
     * @return 是否修改成功
     */
    boolean updateCardName(String packageName, String oldName, String newName) throws Exception;
}
