package com.weird.handler.chatimpl;

import com.alibaba.fastjson.JSONObject;
import com.weird.facade.BroadcastFacade;
import com.weird.handler.ChatHandler;
import com.weird.model.CardPreviewModel;
import com.weird.model.ForbiddenModel;
import com.weird.model.dto.CardListDTO;
import com.weird.model.dto.CardOwnListDTO;
import com.weird.model.dto.UserDataDTO;
import com.weird.model.param.SearchCardParam;
import com.weird.service.CardPreviewService;
import com.weird.service.CardService;
import com.weird.service.ForbiddenService;
import com.weird.service.UserService;
import com.weird.utils.CardPreviewUtil;
import com.weird.utils.PackageUtil;
import com.weird.utils.StringExtendUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

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

    @Autowired
    UserService userService;

    @Autowired
    ForbiddenService forbiddenService;

    final static String SPLIT_STR = ">查诡异 ";

    @Override
    public void handle(JSONObject o) {
        String message = o.getString("raw_message");
        String userQQ =  o.getString("user_id");
        if (message.startsWith(SPLIT_STR)) {
            String originArgs = message.substring(SPLIT_STR.length()).trim();

            // 分页信息
            String cardArgs = originArgs;
            int pageCount = 1;
            Matcher matcher = PAGE_PATTERN.matcher(originArgs);
            if (matcher.matches()) {
                try {
                    pageCount = Integer.parseInt(matcher.group(2));
                    cardArgs = matcher.group(1);
                } catch (NumberFormatException ne) {

                }
            }
            pageCount = Math.max(1, pageCount);
            UserDataDTO userData = userService.getUserByQQ(userQQ);

            // 稀有度信息
            SearchCardParam param = new SearchCardParam();
            if (userData != null) {
                param.setName(userData.getUserName());
            } else {
                param.setName("");
            }
            param.setPage(1);
            param.setPageSize(10);
            List<String> argList = StringExtendUtil.split(cardArgs, " ");
            List<String> searchArgList = new LinkedList<>();
            for (String arg : argList) {
                if (PackageUtil.RARE_LIST.contains(arg)) {
                    if (param.getRareList() == null) {
                        param.setRareList(new LinkedList<>());
                    }
                    param.getRareList().add(arg);
                } else {
                    searchArgList.add(arg);
                }
            }
            cardArgs = String.join(" ", searchArgList);

            String finalCardArgs = cardArgs;
            if (StringUtils.isEmpty(finalCardArgs) && StringUtils.isEmpty(originArgs)) {
                return;
            }
            List<String> cardNameList = cardPreviewService.blurSearch(finalCardArgs);
            if (cardNameList != null && cardNameList.size() == 0) {
                broadcastFacade.sendMsgAsync(buildResponse(String.format("以下条件查不到卡：%s", originArgs), o));
                return;
            }

            param.setCardName(finalCardArgs);
            List<CardListDTO> dbCardList = cardService.selectListUser(param, cardNameList);
            if (CollectionUtils.isEmpty(dbCardList)) {
                broadcastFacade.sendMsgAsync(buildResponse(String.format("以下条件查不到卡：%s", originArgs), o));
                return;
            }
            int listSize = dbCardList.size();
            if (listSize == 1) {
                printCardDetail(dbCardList.get(0), o, userData);
                return;
            }
            CardListDTO firstTarget = dbCardList.stream().filter(c -> c.getCardName().equals(finalCardArgs)).findFirst().orElse(null);
            if (firstTarget != null) {
                printCardDetail(firstTarget, o, userData);
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("共有%d个结果，请从以下卡片中选择1张(一次显示10条)，再次搜索：", listSize));
            int totalPage = listSize / 10 + 1;
            pageCount = (Math.min(totalPage, pageCount) - 1) * 10;
            for (int i = 0; i < 10 && i + pageCount < listSize; ++i) {
                final CardListDTO data = dbCardList.get(i + pageCount);
                sb.append(String.format("\n%d: [%s]%s(%d)", i + pageCount + 1, data.getRare(), data.getCardName(), data.getCount()));
            }
            broadcastFacade.sendMsgAsync(buildResponse(sb.toString(), o));
        }
    }

    private void printCardDetail(CardListDTO card, JSONObject request, UserDataDTO userData) {
        String cardName = card.getCardName();
        CardPreviewModel cardData = cardPreviewService.selectPreviewByName(cardName);
        if (cardData == null) {
            broadcastFacade.sendMsgAsync(buildResponse(String.format("查询到卡片[%s]，但在获取效果时出错。", cardName), request));
            return;
        }

        String cardDesc = CardPreviewUtil.getPreview(cardData);

        int ownCount = 0;
        int selfOwnCount = 0;
        SearchCardParam param = new SearchCardParam();
        param.setCardName(cardName);
        List<CardOwnListDTO> cardOwnList = cardService.selectList(param, Collections.singletonList(cardName));
        for (CardOwnListDTO ownData : cardOwnList) {
            if (!cardName.equals(ownData.getCardName())) {
                continue;
            }
            ownCount += ownData.getCount();
            if (userData != null && ownData.getUserName().equals(userData.getUserName())) {
                selfOwnCount += ownData.getCount();
            }
        }

        List<ForbiddenModel> forbiddenList = forbiddenService.selectAll();
        ForbiddenModel forbid = forbiddenList.stream().filter(c -> card.getCardName().equals(c.getName())).findFirst().orElse(null);

        cardDesc += String.format("\n所属：[%s]%s\n持有数量：%d", card.getRare(), card.getPackageName(), ownCount);
        if (card.getNeedCoin() > 0) {
            if (userData == null || selfOwnCount > 0) {
                cardDesc += String.format("\n需要硬币：%d", card.getNeedCoin());
            } else {
                cardDesc += String.format("\n需要硬币：%d/%d", userData.getCoin(), card.getNeedCoin());
            }
        }
        if (ownCount > 0 && userData != null) {
            cardDesc += String.format("\n你持有：%d", selfOwnCount);
        }
        if (forbid != null) {
            cardDesc += String.format("\n限制数量：%d", forbid.getCount());
        }

        broadcastFacade.sendMsgAsync(buildResponse(cardDesc, request));
    }
}
