package com.weird.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 卡组列表类
 *
 * @author Nidhogg
 * @date 2021.8.14
 */
@Data
public class DeckListModel implements Serializable {
    /**
     * 卡组ID
     */
    int deckId;

    /**
     * 用户ID
     */
    int userId;

    /**
     * 卡组名
     */
    String deckName;

    /**
     * 最后修改时间
     */
    long lastModifyTime;

    /**
     * 分享状态
     */
    int share;

}
