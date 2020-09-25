package com.weird.mapper.main;

import com.weird.model.PackageCardModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PackageCardMapper {
    int deleteByPrimaryKey(Long pk);

    int insert(PackageCardModel record);

    int insertSelective(PackageCardModel record);

    PackageCardModel selectByPrimaryKey(int pk);

    int updateByPrimaryKeySelective(PackageCardModel record);

    int updateByPrimaryKey(PackageCardModel record);

    /**
     * 根据卡包和卡片名搜索卡片信息
     *
     * @param packageId 卡包ID
     * @param name      卡片名
     * @return 查找结果
     */
    List<PackageCardModel> selectInPackage(@Param("packageId") int packageId,
                                           @Param("name") String name);

    /**
     * 根据卡包和卡片名搜索卡片信息
     *
     * @param packageId 卡包ID
     * @param name      卡片名
     * @return 查找结果
     */
    PackageCardModel selectInPackageDistinct(@Param("packageId") int packageId,
                                             @Param("name") String name);

    /**
     * 根据卡名搜索卡片信息
     *
     * @param name 卡片名
     * @return 查找结果
     */
    PackageCardModel selectByNameDistinct(@Param("name") String name);

    /**
     * 根据卡名列表搜索卡片信息
     *
     * @param list 卡片名列表
     * @return 查找结果
     */
    List<String> selectByNameListDistinct(@Param("list") List<String> list);

    /**
     * 批量创建卡片
     *
     * @param packageId 卡包ID
     * @param rare 稀有度
     * @param cardList 卡片名称列表
     * @return 创建数量
     */
    int insertByRareBatch(@Param("packageId") int packageId, @Param("rare") String rare, @Param("list") List<String> cardList);

    List<PackageCardModel> selectRare(@Param("packageId") int packageId);
}