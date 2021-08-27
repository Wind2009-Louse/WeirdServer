package com.weird.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 卡组列表参数
 *
 * @author Nidhogg
 * @date 2021.8.14
 */
@Data
public class DeckListDTO implements Serializable {
    /**
     * 卡组ID
     */
    int deckId;

    /**
     * 卡组名
     */
    String deckName;

    /**
     * 用户名
     */
    String userName;

    /**
     * 最后修改时间
     */
    long lastModifyTime;

    /**
     * 是否分享中
     */
    int share;
}
