package com.weird.service;

import com.weird.model.dto.RollListDTO;
import com.weird.utils.PageResult;

import java.util.List;

public interface RollService {
    /**
     * 根据卡包名和用户名查找抽卡结果
     *
     * @param packageName 卡包名
     * @param userName    用户名
     * @param page        页码
     * @param pageSize    页面大小
     * @return 结果列表
     */
    PageResult<RollListDTO> selectRollList(String packageName,
                                           String userName,
                                           int page,
                                           int pageSize) throws Exception;

    /**
     * 抽卡处理
     *
     * @param cardNames 卡片名
     * @param userName  用户名
     * @return 是否记录成功
     */
    boolean roll(List<String> cardNames, String userName) throws Exception;

    /**
     * 设置抽卡记录状态
     *
     * @param rollId    抽卡记录ID
     * @param newStatus 新的状态
     * @return 是否设置成功
     */
    boolean setStatus(long rollId, int newStatus) throws Exception;
}
