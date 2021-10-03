package com.weird.handler.impl;

import com.weird.handler.CardSearchHandler;
import com.weird.model.enums.CardTypeEnum;
import com.weird.model.param.BlurSearchParam;
import com.weird.utils.CardPreviewUtil;
import org.springframework.stereotype.Component;


/**
 * 刻度处理器
 *
 * @author Sue Sobim
 */
@Component
public class CardSearchScaleHandler implements CardSearchHandler {
    static String[] cardScaleFilter = {"ls:", "scale:", "ls：", "scale：","刻度:","刻度：","灵摆刻度:", "灵摆刻度："};

    @Override
    public boolean handleParam(String realText, BlurSearchParam param) {
        for (String filter : cardScaleFilter) {
            if (realText.startsWith(filter)) {
                String filteredText = realText.substring(filter.length());
                if (CardPreviewUtil.setRangeSearch(
                        filteredText,
                        param,
                        BlurSearchParam::setCardScale,
                        BlurSearchParam::getCardScaleGe,
                        BlurSearchParam::getCardScaleG,
                        BlurSearchParam::getCardScaleLe,
                        BlurSearchParam::getCardScaleL)) {
                    param.getCardTypeSet().add(CardTypeEnum.PENDULUM);
                    return true;
                }
            }
        }
        return false;
    }
}
