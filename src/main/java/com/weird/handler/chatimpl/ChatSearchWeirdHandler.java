package com.weird.handler.chatimpl;

import com.alibaba.fastjson.JSONObject;
import com.weird.facade.BroadcastFacade;
import com.weird.handler.ChatHandler;
import com.weird.model.CardPreviewModel;
import com.weird.model.dto.CardListDTO;
import com.weird.model.param.SearchCardParam;
import com.weird.service.CardPreviewService;
import com.weird.service.CardService;
import com.weird.utils.CardPreviewUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.weird.utils.BroadcastUtil.buildResponse;

/**
 * 诡异查卡
 *
 * @author Nidhogg
 * @date 2021.9.30
 */
@Component
public class ChatSearchWeirdHandler implements ChatHandler {
    @Autowired
    BroadcastFacade broadcastFacade;

    @Autowired
    CardPreviewService cardPreviewService;

    @Autowired
    CardService cardService;

    final static String SPLIT_STR = ">查诡异 ";

    @Override
    public void handle(JSONObject o) {
        String message = o.getString("raw_message");
        if (message.startsWith(SPLIT_STR)) {
            String cardArgs = message.substring(SPLIT_STR.length()).trim();
            if (StringUtils.isEmpty(cardArgs)) {
                return;
            }
            List<String> cardNameList = cardPreviewService.blurSearch(cardArgs);
            if (CollectionUtils.isEmpty(cardNameList)) {
                broadcastFacade.sendMsgAsync(buildResponse(String.format("以下条件查不到卡：%s", cardArgs), o));
                return;
            }
            SearchCardParam param = new SearchCardParam();
            param.setCardName(cardArgs);
            param.setName("");
            param.setPage(1);
            param.setPageSize(10);

            List<CardListDTO> dbCardList = cardService.selectListUser(param, cardNameList);
            if (CollectionUtils.isEmpty(dbCardList)) {
                broadcastFacade.sendMsgAsync(buildResponse(String.format("以下条件查不到卡：%s", cardArgs), o));
                return;
            }
            int listSize = dbCardList.size();
            if (listSize == 1) {
                printCardDetail(dbCardList.get(0), o);
                return;
            }
            CardListDTO firstTarget = dbCardList.stream().filter(c -> c.getCardName().equals(cardArgs)).findFirst().orElse(null);
            if (firstTarget != null) {
                printCardDetail(firstTarget, o);
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("共有%d个结果，请从以下卡片中选择1张(只显示前10条)，再次搜索：", listSize));
            for (int i = 0; i < 10 && i < listSize; ++i) {
                sb.append(String.format("\n%d: [%s]%s", i + 1, dbCardList.get(i).getRare(), dbCardList.get(i).getCardName()));
            }
            broadcastFacade.sendMsgAsync(buildResponse(sb.toString(), o));
        }
    }

    private void printCardDetail(CardListDTO card, JSONObject request) {
        String cardName = card.getCardName();
        CardPreviewModel cardData = cardPreviewService.selectPreviewByName(cardName);
        if (cardData == null) {
            broadcastFacade.sendMsgAsync(buildResponse(String.format("查询到卡片[%s]，但在获取效果时出错。", cardName), request));
            return;
        }

        String cardDesc = CardPreviewUtil.getPreview(cardData);
        int ownCount = cardService.getCardOwnCountByCardName(card.getCardName());

        cardDesc += String.format("\n所属：[%s]%s\n持有数量：%d", card.getRare(), card.getPackageName(), ownCount);
        if (card.getNeedCoin() > 0) {
            cardDesc += String.format("\n需要硬币：%d", card.getNeedCoin());
        }

        broadcastFacade.sendMsgAsync(buildResponse(cardDesc, request));
    }
}
