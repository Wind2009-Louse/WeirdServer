package com.weird.handler.chatimpl;

import com.weird.facade.BroadcastFacade;
import com.weird.handler.ChatHandler;
import com.weird.model.CardPreviewModel;
import com.weird.service.CardPreviewService;
import com.weird.utils.CardPreviewUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 查卡
 *
 * @author Nidhogg
 * @date 2021.9.30
 */
@Component
public class ChatSearchCardHandler implements ChatHandler {
    @Autowired
    BroadcastFacade broadcastFacade;

    @Autowired
    CardPreviewService cardPreviewService;

    final static String SPLIT_STR = ">查卡 ";

    @Override
    public void handle(String message) {
        if (message.startsWith(SPLIT_STR)) {
            String cardArgs = message.substring(SPLIT_STR.length()).trim();
            if (StringUtils.isEmpty(cardArgs)) {
                return;
            }
            List<String> cardNameList = cardPreviewService.blurSearch(cardArgs);
            final int listSize = cardNameList.size();
            if (CollectionUtils.isEmpty(cardNameList)) {
                broadcastFacade.sendMsgAsync(String.format("以下条件查不到卡：%s", cardArgs));
            } else if (cardNameList.contains(cardArgs)) {
                searchByName(cardArgs);
            } else if (listSize > 1) {
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("共有%d个结果，请从以下卡片中选择1张(只显示前10条)，再次搜索：", listSize));
                for (int i = 0; i < 10 && i < listSize; ++i) {
                    sb.append(String.format("\n%d: %s", i + 1, cardNameList.get(i)));
                }
                broadcastFacade.sendMsgAsync(sb.toString());
            } else {
                String cardName = cardNameList.get(0);
                searchByName(cardName);
            }
        }
    }

    private void searchByName(String cardName) {
        CardPreviewModel cardData = cardPreviewService.selectPreviewByName(cardName);
        if (cardData == null) {
            broadcastFacade.sendMsgAsync(String.format("查询到卡片[%s]，但在获取效果时出错。", cardName));
        } else {
            broadcastFacade.sendMsgAsync(CardPreviewUtil.getPreview(cardData));
        }
    }
}
