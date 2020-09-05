package com.weird.service;

public interface CardService {
    /**
     * 修改用户持有的卡片数量
     *
     * @param userName 用户名
     * @param packageName 卡包名
     * @param cardName 卡片名
     * @param count 新的卡片数量
     * @return 是否修改成功
     */
    boolean updateCardCount(String userName, String packageName, String cardName, int count) throws Exception;
}
