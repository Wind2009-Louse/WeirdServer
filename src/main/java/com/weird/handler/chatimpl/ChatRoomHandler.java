package com.weird.handler.chatimpl;

import com.alibaba.fastjson.JSONObject;
import com.weird.facade.BroadcastFacade;
import com.weird.handler.ChatHandler;
import com.weird.model.bo.ChatRoomBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.weird.utils.BroadcastUtil.buildResponse;

/**
 * 房间记录
 *
 * @author Nidhogg
 * @date 2021.9.30
 */
@Component
public class ChatRoomHandler implements ChatHandler {
    @Autowired
    BroadcastFacade broadcastFacade;

    static Map<String, List<ChatRoomBO>> roomMap = new HashMap<>();

    final static String CALL_STR = ">查房";

    final static String RECORD_STR = "M#";

    final static long TIME_GAP = 1000 * 60 * 90;

    final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

    @Override
    public void handle(JSONObject o) {
        String message = o.getString("raw_message");
        if (!"group".equals(o.getString("message_type"))) {
            return;
        }
        String groupId = o.getString("group_id");
        if (!roomMap.containsKey(groupId)) {
            roomMap.put(groupId, new LinkedList<>());
        }
        List<ChatRoomBO> roomList = roomMap.getOrDefault(groupId, Collections.emptyList());
        if (message.contains(RECORD_STR)) {
            ChatRoomBO chatRoomBO = new ChatRoomBO();
            chatRoomBO.setChatTime(System.currentTimeMillis());
            chatRoomBO.setUserName(o.getJSONObject("sender").getString("card"));
            chatRoomBO.setDetail(message);
            roomList.add(chatRoomBO);
        } else if (CALL_STR.equals(message)) {
            StringBuilder sb = new StringBuilder();
            sb.append("最近90分钟内的房间记录：");
            List<String> historyList = getRoomList(roomList);
            if (CollectionUtils.isEmpty(historyList)) {
                sb.append("\n无");
            } else {
                for (String str : historyList) {
                    sb.append(str);
                }
            }

            broadcastFacade.sendMsgAsync(buildResponse(sb.toString(), o));
        }
    }

    private synchronized List<String> getRoomList(List<ChatRoomBO> roomList) {
        List<String> historyList = new ArrayList<>();
        Iterator<ChatRoomBO> iterator = roomList.iterator();
        while (iterator.hasNext()) {
            ChatRoomBO chatRoomBO = iterator.next();
            long chatTime = chatRoomBO.getChatTime();
            if (System.currentTimeMillis() - chatTime > TIME_GAP) {
                iterator.remove();
                continue;
            }
            historyList.add(0, String.format("\n[%s]%s: %s", TIME_FORMAT.format(new Date(chatTime)), chatRoomBO.getUserName(), chatRoomBO.getDetail()));
        }
        return historyList;
    }
}
