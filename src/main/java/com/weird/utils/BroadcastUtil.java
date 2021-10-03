package com.weird.utils;

import com.weird.model.bo.RollBroadcastBO;
import com.weird.model.dto.RollListDTO;
import org.springframework.util.comparator.Comparators;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 广播工具包
 *
 * @author Nidhogg
 * @date 2021.9.30
 */
public class BroadcastUtil {
    /**
     * 根据查询到的抽卡信息，统计抽卡数据
     *
     * @param totalBO   总计数据
     * @param userBoMap 用户数据Map
     * @param deckBoMap 卡包数据Map
     * @param dataList  总抽卡数据
     */
    static public void calculateRollResult(
            RollBroadcastBO totalBO,
            Map<String, RollBroadcastBO> userBoMap,
            Map<String, RollBroadcastBO> deckBoMap,
            List<RollListDTO> dataList
    ) {
        totalBO.setName("合计");
        for (RollListDTO dto : dataList) {
            if (dto.getIsDisabled() >= 1 || dto.getRollResult().size() != 3) {
                continue;
            }
            boolean isRare = dto.getRollResult().stream().anyMatch(o -> !PackageUtil.NR_LIST.contains(o.getRare()));
            RollBroadcastBO userBO = userBoMap.getOrDefault(dto.getRollUserName(), null);
            if (userBO == null) {
                userBO = new RollBroadcastBO(dto.getRollUserName());
                userBoMap.put(dto.getRollUserName(), userBO);
            }
            RollBroadcastBO deckBO = deckBoMap.getOrDefault(dto.getRollPackageName(), null);
            if (deckBO == null) {
                deckBO = new RollBroadcastBO(String.format("[%s]", dto.getRollPackageName()));
                deckBoMap.put(dto.getRollPackageName(), deckBO);
            }
            RollBroadcastBO[] boList = {totalBO, userBO, deckBO};
            for (RollBroadcastBO bo : boList) {
                bo.setTotalCount(bo.getTotalCount() + 1);
                if (isRare) {
                    bo.setRareCount(bo.getRareCount() + 1);
                }
            }
        }
    }

    static public String buildRollTable(Collection<RollBroadcastBO> targetList, String tableName) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s\t闪数\t总数\t闪率", tableName));
        targetList.stream().sorted((o1, o2) -> Comparators.comparable().compare(o2.getRareRate(), o1.getRareRate())).forEach(o -> {
            sb.append(String.format("\n%s\t%d\t%d\t%.2f%%", o.getName(), o.getRareCount(), o.getTotalCount(), o.getRareRate()));
        });

        return sb.toString();
    }
}