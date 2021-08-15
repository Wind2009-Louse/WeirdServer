package com.weird.handler.impl;

import com.weird.handler.CardSearchHandler;
import com.weird.model.enums.CardRaceEnum;
import com.weird.model.param.BlurSearchParam;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


/**
 * 名称处理器
 *
 * @author Sue Sobim
 */
@Component
public class CardSearchNameHandler implements CardSearchHandler {
    static String[] cardRaceFilter = {"n:", "name:", "名称:", "名字:", "卡名:", "n：", "name：", "名称：", "名字：", "卡名："};

    @Override
    public boolean handleParam(String realText, BlurSearchParam param) {
        for (String filter : cardRaceFilter) {
            if (realText.startsWith(filter)) {
                String filteredText = realText.substring(filter.length());
                if (!StringUtils.isEmpty(filteredText)) {
                    param.getCardNameList().add(filteredText);
                    return true;
                }
            }
        }
        return false;
    }
}
