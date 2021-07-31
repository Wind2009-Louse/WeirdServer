package com.weird.service;

import com.weird.model.enums.LoginTypeEnum;
import com.weird.model.param.CollectionParam;
import com.weird.utils.OperationException;

import java.util.List;

/**
 * 用户收藏服务
 *
 * @author Nidhogg
 * @date 2021.7.28
 *
 */
public interface CollectionService {
    /**
     * 获取用户收藏列表
     *
     * @param userName 用户名
     * @return 收藏卡片ID列表
     */
    List<Integer> getCollectionByName(String userName) throws OperationException;

    /**
     * 添加收藏/移除收藏操作
     *
     * @param param
     * @return
     * @throws OperationException
     */
    boolean operation(CollectionParam param, LoginTypeEnum loginTypeEnum) throws OperationException;
}
