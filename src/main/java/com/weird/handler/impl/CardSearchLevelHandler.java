package com.weird.handler.impl;

import com.weird.handler.CardSearchHandler;
import com.weird.model.enums.CardTypeEnum;
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
    static String[] cardLevelFilter = {"l:", "lv:", "level:", "l：", "lv：", "level：", "等级:", "等级：", "阶级：", "阶级:", "连接：", "连接:"};

    static String[] rankFilter = {"阶级：", "阶级:"};

    static String[] linkFilter = {"连接：", "连接:"};

    @Override
    public boolean handleParam(String realText, BlurSearchParam param) {
        for (String filter : rankFilter) {
            if (realText.startsWith(filter)) {
                param.getCardTypeSet().add(CardTypeEnum.XYZ);
            }
        }
        for (String filter : linkFilter) {
            if (realText.startsWith(filter)) {
                param.getCardTypeSet().add(CardTypeEnum.LINK);
            }
        }

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
