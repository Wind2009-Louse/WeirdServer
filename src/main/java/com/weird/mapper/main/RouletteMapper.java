package com.weird.mapper.main;

import com.weird.model.RouletteConfigModel;
import com.weird.model.dto.RouletteConfigDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RouletteMapper {
    /**
     * 获取配置列表
     *
     * @return 配置列表
     */
    List<RouletteConfigDTO> selectConfigList();

    /**
     * 清除配置
     *
     * @return 清除数量
     */
    int clearConfig();

    /**
     * 批量插入配置
     *
     * @param list 配置列表
     * @return 插入数量
     */
    int batchInsertConfig(@Param("list") List<RouletteConfigModel> list);
}
