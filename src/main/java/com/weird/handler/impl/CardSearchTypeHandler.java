package com.weird.handler.impl;

import com.weird.handler.CardSearchHandler;
import com.weird.model.enums.CardTypeEnum;
import com.weird.model.param.BlurSearchParam;
import org.springframework.stereotype.Component;


/**
 * 类型处理器
 *
 * @author Sue Sobim
 */
@Component
public class CardSearchTypeHandler implements CardSearchHandler {
    static String[] cardTypeFilter = {"t:", "type:", "t：", "type：", "类型:", "类型："};

    @Override
    public boolean handleParam(String realText, BlurSearchParam param) {
        for (String filter : cardTypeFilter) {
            if (realText.startsWith(filter)) {
                String filteredText = realText.substring(filter.length());
                CardTypeEnum cardType = CardTypeEnum.getByName(filteredText);
                if (cardType != null) {
                    param.getCardTypeSet().add(cardType);
                    return true;
                }
            }
        }
        return false;
    }
}
