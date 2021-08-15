package com.weird.handler.impl;

import com.weird.handler.CardSearchHandler;
import com.weird.model.param.BlurSearchParam;
import com.weird.utils.CardPreviewUtil;
import org.springframework.stereotype.Component;

/**
 * 攻击处理器
 *
 * @author Sue Sobim
 */
@Component
public class CardSearchAttackHandler implements CardSearchHandler {
    static String[] cardAttackFilter = {"atk:", "attack:", "atk：", "attack：", "攻击力:", "攻击力：", "攻击:", "攻击："};

    @Override
    public boolean handleParam(String realText, BlurSearchParam param) {
        for (String filter : cardAttackFilter) {
            if (realText.startsWith(filter)) {
                String filteredText = realText.substring(filter.length());
                if (CardPreviewUtil.setRangeSearch(
                        filteredText,
                        param,
                        BlurSearchParam::setCardAttack,
                        BlurSearchParam::getCardAttackGe,
                        BlurSearchParam::getCardAttackG,
                        BlurSearchParam::getCardAttackLe,
                        BlurSearchParam::getCardAttackL)) {
                    return true;
                }
            }
        }
        return false;
    }
}
