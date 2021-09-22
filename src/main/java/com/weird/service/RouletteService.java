package com.weird.service;

import com.weird.model.dto.RouletteConfigDTO;
import com.weird.model.dto.RouletteHistoryDTO;
import com.weird.model.dto.RouletteResultDTO;
import com.weird.model.param.PageParam;
import com.weird.utils.OperationException;
import com.weird.utils.PageResult;

import java.util.List;

/**
 * 转盘服务
 *
 * @author Nidhogg
 * @date 2021.9.22
 */
public interface RouletteService {
    /**
     * 显示当前的转盘配置
     *
     * @return
     */
    List<RouletteConfigDTO> listConfig();

    /**
     * 更新转盘配置
     *
     * @param list     配置列表
     * @param operator 操作人
     * @return 更新结果
     */
    String updateConfig(List<RouletteConfigDTO> list, String operator) throws OperationException;

    /**
     * 进行转盘
     *
     * @param userName 转盘用户
     * @return 转盘结果
     */
    RouletteResultDTO roulette(String userName) throws OperationException;

    /**
     * 转盘历史查询
     *
     * @param param
     * @return
     */
    PageResult<RouletteHistoryDTO> history(PageParam param) throws Exception;
}
