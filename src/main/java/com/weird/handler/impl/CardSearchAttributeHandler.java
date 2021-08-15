package com.weird.handler.impl;

import com.weird.handler.CardSearchHandler;
import com.weird.model.enums.CardAttributeEnum;
import com.weird.model.param.BlurSearchParam;
import org.springframework.stereotype.Component;

/**
 * 属性处理器
 *
 * @author Sue Sobim
 */
@Component
public class CardSearchAttributeHandler implements CardSearchHandler {
    static String[] cardAttributeFilter = {"a:", "attribute:", "a：", "attribute：", "属性:", "属性："};

    @Override
    public boolean handleParam(String realText, BlurSearchParam param) {
        for (String filter : cardAttributeFilter) {
            if (realText.startsWith(filter)) {
                String filteredText = realText.substring(filter.length());
                CardAttributeEnum cardAttribute = CardAttributeEnum.getByName(filteredText);
                if (cardAttribute != null) {
                    param.getCardAttributeSet().add(cardAttribute);
                    return true;
                }
            }
        }
        return false;
    }
}
