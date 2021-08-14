package com.weird.model.param;

import com.weird.interfaces.Fixable;
import com.weird.model.dto.DeckInfoDTO;
import lombok.Data;

import java.io.Serializable;

/**
 * 提交卡组的参数
 *
 * @author Nidhogg
 * @date 2021.8.14
 */
@Data
public class DeckSubmitParam extends UserCheckParam implements Serializable, Fixable {
    DeckInfoDTO deck;

    @Override
    public void fix() {
        if (deck == null) {
            deck = new DeckInfoDTO();
        }
    }
}
