package com.weird.handler.impl;

import com.weird.handler.CardSearchHandler;
import com.weird.model.enums.CardRaceEnum;
import com.weird.model.param.BlurSearchParam;
import org.springframework.stereotype.Component;


/**
 * 种族处理器
 *
 * @author Sue Sobim
 */
@Component
public class CardSearchRaceHandler implements CardSearchHandler {
    static String[] cardRaceFilter = {"r:", "race:", "r：", "race：", "种族:", "种族："};

    @Override
    public boolean handleParam(String realText, BlurSearchParam param) {
        for (String filter : cardRaceFilter) {
            if (realText.startsWith(filter)) {
                String filteredText = realText.substring(filter.length());
                CardRaceEnum cardRace = CardRaceEnum.getByName(filteredText);
                if (cardRace != null) {
                    param.getCardRaceSet().add(cardRace);
                    return true;
                }
            }
        }
        return false;
    }
}
