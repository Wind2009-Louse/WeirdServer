package com.weird.model.param;

import com.weird.interfaces.Fixable;
import com.weird.model.dto.DeckInfoDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 批量提交卡组的参数
 *
 * @author Nidhogg
 * @date 2021.8.14
 */
@Data
public class DeckSubmitBatchParam extends UserCheckParam implements Serializable, Fixable {
    List<DeckInfoDTO> deckList;

    @Override
    public void fix() {
        if (deckList == null) {
            deckList = Collections.emptyList();
        }
    }
}
