package com.weird.handler.chatimpl;

import com.alibaba.fastjson.JSONObject;
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
import java.util.regex.Matcher;

import static com.weird.utils.BroadcastUtil.MESSAGE;
import static com.weird.utils.BroadcastUtil.buildResponse;

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
    public void handle(JSONObject o) {
        String message = o.getString(MESSAGE);
        if (message.startsWith(SPLIT_STR)) {
            String cardArgs = message.substring(SPLIT_STR.length()).trim();
            int pageCount = 1;
            Matcher matcher = PAGE_PATTERN.matcher(cardArgs);
            if (matcher.matches()) {
                try {
                    pageCount = Integer.parseInt(matcher.group(2));
                    cardArgs = matcher.group(1);
                } catch (NumberFormatException ne) {

                }
            }
            pageCount = Math.max(1, pageCount);

            if (StringUtils.isEmpty(cardArgs)) {
                return;
            }
            List<String> cardNameList = cardPreviewService.blurSearch(cardArgs);
            final int listSize = cardNameList.size();
            if (CollectionUtils.isEmpty(cardNameList)) {
                broadcastFacade.sendMsgAsync(buildResponse(String.format("以下条件查不到卡：%s", cardArgs), o));
            } else if (cardNameList.contains(cardArgs)) {
                searchByName(cardArgs, o);
            } else if (listSize > 1) {
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("共有%d个结果，请从以下卡片中选择1张(一次显示10条)，再次搜索：", listSize));
                int totalPage = listSize / 10 + 1;
                pageCount = (Math.min(totalPage, pageCount) - 1) * 10;
                for (int i = 0; i < 10 && i + pageCount < listSize; ++i) {
                    sb.append(String.format("\n%d: %s", i + pageCount + 1, cardNameList.get(i + pageCount)));
                }
                broadcastFacade.sendMsgAsync(buildResponse(sb.toString(), o));
            } else {
                String cardName = cardNameList.get(0);
                searchByName(cardName, o);
            }
        }
    }

    private void searchByName(String cardName, JSONObject request) {
        CardPreviewModel cardData = cardPreviewService.selectPreviewByName(cardName);
        if (cardData == null) {
            broadcastFacade.sendMsgAsync(buildResponse(String.format("查询到卡片[%s]，但在获取效果时出错。", cardName), request));
        } else {
            broadcastFacade.sendMsgAsync(buildResponse(CardPreviewUtil.getPreview(cardData, true, false), request));
        }
    }
}
