package com.weird.handler.impl;

import com.weird.handler.CardSearchHandler;
import com.weird.model.param.BlurSearchParam;
import com.weird.utils.CardPreviewUtil;
import org.springframework.stereotype.Component;

/**
 * 守备处理器
 *
 * @author Sue Sobim
 */
@Component
public class CardSearchDefenseHandler implements CardSearchHandler {
    static String[] cardDefenseFilter = {"def:", "defense:", "def：", "defense：", "守备力:", "守备力：", "守备:", "守备：", "防御力:", "防御力：", "防御:", "防御："};

    @Override
    public boolean handleParam(String realText, BlurSearchParam param) {
        for (String filter : cardDefenseFilter) {
            if (realText.startsWith(filter)) {
                String filteredText = realText.substring(filter.length());
                if (CardPreviewUtil.setRangeSearch(
                        filteredText,
                        param,
                        BlurSearchParam::setCardDefense,
                        BlurSearchParam::getCardDefenseGe,
                        BlurSearchParam::getCardDefenseG,
                        BlurSearchParam::getCardDefenseLe,
                        BlurSearchParam::getCardDefenseL)) {
                    return true;
                }
            }
        }
        return false;
    }
}
