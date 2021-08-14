package com.weird.model.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询卡组信息
 *
 * @author Nidhogg
 * @date 2021.8.14
 */
@Data
public class DeckInfoParam extends UserCheckParam implements Serializable {
    /**
     * 卡组ID
     */
    int deckId;
}
