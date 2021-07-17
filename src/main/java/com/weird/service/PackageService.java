package com.weird.service;

import com.weird.model.PackageInfoModel;
import com.weird.model.param.BatchAddCardParam;
import com.weird.utils.OperationException;

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
    boolean addPackage(String name, String operator) throws Exception;

    /**
     * 更新卡包名称
     *
     * @param oldName 旧卡包名称
     * @param newName 新卡包名称
     * @return 是否更新成功
     */
    boolean updatePackageName(String oldName, String newName, String operator) throws Exception;

    /**
     * 在卡包中添加卡片
     *
     * @param packageName 卡包名
     * @param cardName    卡片名
     * @param rare        稀有度
     * @return 是否添加成功
     */
    boolean addCard(String packageName, String cardName, String rare, String operator) throws Exception;

    /**
     * 在卡包中批量添加卡片
     *
     * @param param       批量添加参数
     * @param allCardList 所有卡片的名称列表
     * @return 返回结果
     */
    String addCardList(BatchAddCardParam param, List<String> allCardList);

    /**
     * 修改卡片名字
     *
     * @param oldName 旧卡名
     * @param newName 新卡名
     * @param newRare 新稀有度
     * @param isShow  是否在历史记录中显示该卡片
     * @return 是否修改成功
     */
    boolean updateCardName(String oldName, String newName, String newRare, int isShow, String operator) throws Exception;

    /**
     * 互换卡包中两张卡的稀有度
     *
     * @param name1  卡名1
     * @param name2  卡名2
     * @param isShow 是否展示
     * @return 是否修改成功
     */
    boolean exchangeCardName(String name1, String name2, int isShow, String operator) throws Exception;

    /**
     * 对卡包进行排序
     *
     * @param packageList 卡包ID列表
     * @return 排序结果
     */
    String sort(List<Integer> packageList) throws OperationException;
}
