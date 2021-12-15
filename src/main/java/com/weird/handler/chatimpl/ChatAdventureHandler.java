package com.weird.handler.chatimpl;

import com.alibaba.fastjson.JSONObject;
import com.weird.facade.BroadcastFacade;
import com.weird.handler.ChatHandler;
import com.weird.model.enums.AdventureEnum;
import com.weird.utils.OperationException;
import com.weird.utils.StringExtendUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

import static com.weird.model.enums.AdventureEnumConst.ADVENTURE_ROUND;
import static com.weird.model.enums.AdventureEnumConst.PER_ROUND;
import static com.weird.utils.BroadcastUtil.MESSAGE;
import static com.weird.utils.BroadcastUtil.buildResponse;

/**
 * 查询冒险地图
 *
 * @author Nidhogg
 * @date 2021.9.30
 */
@Component
public class ChatAdventureHandler implements ChatHandler {
    @Autowired
    BroadcastFacade broadcastFacade;

    final static List<String> SPLIT_STR_LIST = Arrays.asList(">查冒险 ", ">查毛线 ", ">冒险 ", ">毛线 ");

    @Override
    public void handle(JSONObject o) {
        String message = o.getString(MESSAGE);
        for (String splitStr : SPLIT_STR_LIST) {
            if (message.startsWith(splitStr)) {
                String args = message.substring(splitStr.length()).trim();
                List<String> argList = StringExtendUtil.split(args, " ");
                AdventureEnum currentSpace;
                int step = 0;

                if (argList.size() > 2 || CollectionUtils.isEmpty(argList)) {
                    broadcastFacade.sendMsgAsync(buildResponse("请输入正确格式：\n>查冒险 当前地点 [前进格数]", o));
                    return;
                }
                try {
                    currentSpace = AdventureEnum.getDistinctByName(argList.get(0));
                    if (argList.size() > 1) {
                        step = Integer.parseInt(argList.get(1));
                        if (step > 6 || step < -6) {
                            throw new OperationException("前进格数必须为-6~6！");
                        }
                    }
                } catch (OperationException oe) {
                    broadcastFacade.sendMsgAsync(buildResponse(oe.getMessage(), o));
                    return;
                } catch (Exception e) {
                    broadcastFacade.sendMsgAsync(buildResponse("请输入正确格式：\n>查冒险 当前地点 [前进格数]", o));
                    return;
                }

                int currentIndex = currentSpace.getIndex();
                int nextIndex = (currentIndex + step) % ADVENTURE_ROUND;
                AdventureEnum nextSpace = AdventureEnum.getDistinctByIndex(nextIndex);
                if (nextSpace != null) {
                    String info = nextSpace.getInfo();
                    if (step > 0 && nextIndex < currentIndex) {
                        info += PER_ROUND;
                    }
                    broadcastFacade.sendMsgAsync(buildResponse(info, o));
                }
            }
        }
    }
}
