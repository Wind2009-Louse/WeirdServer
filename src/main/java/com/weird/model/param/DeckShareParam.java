package com.weird.model.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 分享卡组的参数
 *
 * @author Nidhogg
 * @date 2021.8.14
 */
@Data
public class DeckShareParam extends UserCheckParam implements Serializable {
    /**
     * 卡组ID
     */
    int deckId;

    /**
     * 是否分享（0=分享，1=不分享）
     */
    int share;
}
