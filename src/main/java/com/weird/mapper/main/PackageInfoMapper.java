package com.weird.mapper.main;

import com.weird.model.PackageInfoModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PackageInfoMapper {
    int deleteByPrimaryKey(Integer packageId);

    int insert(PackageInfoModel record);

    int insertSelective(PackageInfoModel record);

    PackageInfoModel selectByPrimaryKey(Integer packageId);

    int updateByPrimaryKeySelective(PackageInfoModel record);

    int updateByPrimaryKey(PackageInfoModel record);

    /**
     * 根据卡包名搜索卡包信息
     *
     * @param packageName 卡包名
     * @return 卡包信息
     */
    PackageInfoModel selectByNameDistinct(String packageName);

    /**
     * 根据卡包名搜索卡包列表
     *
     * @param packageName 卡包名
     * @return 卡包列表
     */
    List<PackageInfoModel> selectByName(String packageName);

    /**
     * 根据卡包名列表搜索卡包列表ID
     *
     * @param packageNameList 卡包名列表
     * @return 卡包列表ID
     */
    List<Integer> selectByNameList(@Param("packageNameList") List<String> packageNameList);

    /**
     * 清除原有的排序号
     *
     * @return 更改数量
     */
    int clearOrder();

    /**
     * 更新排序号
     *
     * @param packageId 卡包号
     * @param orderNum  排序号
     * @return
     */
    int updateOrder(@Param("packageId") int packageId, @Param("orderNum") int orderNum);
}