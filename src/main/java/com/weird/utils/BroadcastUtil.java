package com.weird.utils;

import com.alibaba.fastjson.JSONObject;
import com.weird.model.bo.RollBroadcastBO;
import com.weird.model.dto.RollListDTO;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 广播工具包
 *
 * @author Nidhogg
 * @date 2021.9.30
 */
@Slf4j
public class BroadcastUtil {
    static public String NOT_BIND_WARNING = "你暂未绑定帐号，请私聊Bot（群内回复有泄漏密码风险）使用以下指令进行绑定！\n>认证 用户名 密码";

    static public Pattern AT_PATTERN = Pattern.compile("\\[CQ:at,qq=(\\d+)]");

    static public SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

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
        targetList.stream().sorted(Comparator.comparingDouble(RollBroadcastBO::getRareRate).thenComparingLong(RollBroadcastBO::getTotalCount).reversed()).forEach(o -> {
            sb.append(String.format("\n%s\t%d\t%d\t%.2f%%", o.getName(), o.getRareCount(), o.getTotalCount(), o.getRareRate()));
        });

        return sb.toString();
    }

    static public JSONObject buildResponse(String msg, JSONObject request) {
        return buildResponse(msg, request, false);
    }

    static public JSONObject buildResponse(String msg, JSONObject request, boolean at) {
        if (at) {
            boolean inGroup = "group".equals(request.getString("message_type"));
            if (inGroup) {
                msg = String.format("[CQ:at,qq=%s] ", request.getString("user_id")) + msg;
            }
        }
        JSONObject response = new JSONObject();
        response.put("message", msg);

        String messageType = request.getString("message_type");
        if (request.containsKey("group_id")) {
            response.put("group_id", request.get("group_id"));
        }
        if (request.containsKey("user_id")) {
            response.put("user_id", request.get("user_id"));
        }
        switch (messageType) {
            case "group":
                response.put("message_type", "group");
                break;
            case "private":
                response.put("message_type", "private");
                break;
            default:
                log.warn("无法对{}回复[{}]。", request.toJSONString(), msg);
                return null;
        }

        return response;
    }
}
