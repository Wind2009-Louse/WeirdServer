package com.weird.handler.impl;

import com.weird.handler.CardSearchHandler;
import com.weird.model.param.BlurSearchParam;
import com.weird.utils.CardPreviewUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 等级处理器
 *
 * @author Sue Sobim
 */
@Component
public class CardSearchLevelHandler implements CardSearchHandler {
    static String[] cardLevelFilter = {"l:", "lv:", "level:", "l：", "lv：", "level：", "等级:", "等级："};

    @Override
    public boolean handleParam(String realText, BlurSearchParam param) {
        for (String filter : cardLevelFilter) {
            if (realText.startsWith(filter)) {
                String filteredText = realText.substring(filter.length());
                if (CardPreviewUtil.setRangeSearch(
                        filteredText,
                        param,
                        BlurSearchParam::setCardLevel,
                        BlurSearchParam::getCardLevelGe,
                        BlurSearchParam::getCardLevelG,
                        BlurSearchParam::getCardLevelLe,
                        BlurSearchParam::getCardLevelL)) {
                    return true;
                }
            }
        }
        return false;
    }
}
