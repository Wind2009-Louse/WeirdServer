package com.weird.handler.impl;

import com.weird.handler.CardSearchHandler;
import com.weird.model.param.BlurSearchParam;
import org.springframework.stereotype.Component;


/**
 * 刻度处理器
 *
 * @author Sue Sobim
 */
@Component
public class CardSearchScaleHandler implements CardSearchHandler {
    static String[] cardScaleFilter = {"ls:", "scale:", "ls=", "scale="};

    @Override
    public boolean handleParam(String realText, BlurSearchParam param) {
        for (String filter : cardScaleFilter) {
            if (realText.startsWith(filter)) {
                String filteredText = realText.substring(filter.length());
                if ("?".equals(filteredText)) {
                    param.setCardScale(-2L);
                    return true;
                } else {
                    try {
                        param.setCardScale(Long.parseLong(filteredText));
                        return true;
                    } catch (Exception e) {
                        param.setCardScale(null);
                    }
                }
            }
        }
        return false;
    }
}
