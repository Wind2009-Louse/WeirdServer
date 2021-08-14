package com.weird.model.dto;

import com.weird.model.enums.DeckCardTypeEnum;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * 卡组信息DTO
 *
 * @author Nidhogg
 * @date 2021.8.14
 */
@Data
public class DeckInfoDTO implements Serializable {
    /**
     * 卡组ID
     */
    int deckId;

    /**
     * 卡组名
     */
    String deckName;

    /**
     * 卡组用户ID
     */
    int userId;

    /**
     * 卡组用户
     */
    String userName;

    /**
     * 最后修改时间
     */
    long lastModifyTime;

    /**
     * 主卡组列表
     */
    List<DeckCardDTO> mainList;

    /**
     * 额外卡组列表
     */
    List<DeckCardDTO> exList;

    /**
     * side列表
     */
    List<DeckCardDTO> sideList;

    /**
     * 拼成的YDK字符串
     */
    String ydk;

    public boolean checkDeck() {
        return checkList(mainList, 60) || checkList(exList, 15) || checkList(sideList, 15) || StringUtils.isEmpty(deckName);
    }

    private boolean checkList(List<DeckCardDTO> list, int maxCount) {
        for (DeckCardDTO card : list) {
            if (card.count <= 0) {
                return false;
            }
            maxCount -= card.count;
        }
        return maxCount >= 0;
    }

    public void buildDeckList() {
        if (!StringUtils.isEmpty(ydk)) {
            Map<Long, Integer> mainCount = new LinkedHashMap<>(60);
            Map<Long, Integer> exCount = new LinkedHashMap<>(15);
            Map<Long, Integer> sideCount = new LinkedHashMap<>(15);
            Map<Integer, Map<Long, Integer>> countMapByType = new HashMap<>(3);
            countMapByType.put(DeckCardTypeEnum.MAIN.getId(), mainCount);
            countMapByType.put(DeckCardTypeEnum.EX.getId(), exCount);
            countMapByType.put(DeckCardTypeEnum.SIDE.getId(), sideCount);

            DeckCardTypeEnum currentType = DeckCardTypeEnum.MAIN;
            for (String rowLine : ydk.split("\n")) {
                String line = rowLine.trim();

                // 判断是否更改卡片类型
                boolean changeType = false;
                for (DeckCardTypeEnum allType : DeckCardTypeEnum.values()) {
                    if (line.contains(allType.getName())) {
                        currentType = allType;
                        changeType = true;
                        break;
                    }
                }
                long code = 0;
                try {
                    code = Long.parseLong(line);
                } catch (NumberFormatException e) {
                }
                if (code <= 0) {
                    changeType = true;
                }
                if (changeType) {
                    continue;
                }

                Map<Long, Integer> currentMap = countMapByType.getOrDefault(currentType.getId(), null);
                if (currentMap == null) {
                    continue;
                }
                Integer currentCount = currentMap.getOrDefault(code, 0);
                currentMap.put(code, currentCount + 1);
            }
            mainList = transDeckCardList(mainCount);
            exList = transDeckCardList(exCount);
            sideList = transDeckCardList(sideCount);
        }
    }

    private List<DeckCardDTO> transDeckCardList(Map<Long, Integer> map) {
        List<DeckCardDTO> result = new ArrayList<>(60);
        for (Map.Entry<Long, Integer> entry : map.entrySet()) {
            Long code = entry.getKey();
            Integer count = entry.getValue();
            result.add(new DeckCardDTO(code, count));
        }
        return result;
    }

    public void buildYdk() {
        StringBuilder sb = new StringBuilder();
        sb.append("#created by WeirdServer\n").append("#main\n");
        putCardInBuilder(mainList, sb);
        sb.append("#extra\n");
        putCardInBuilder(exList, sb);
        sb.append("!side\n");
        putCardInBuilder(sideList, sb);
        ydk = sb.toString();
    }

    public void putCardInBuilder(List<DeckCardDTO> cardList, StringBuilder sb) {
        for (DeckCardDTO card : cardList) {
            for (int count = 0; count < card.getCount(); ++count) {
                sb.append(card.getCode()).append("\n");
            }
        }
    }
}
