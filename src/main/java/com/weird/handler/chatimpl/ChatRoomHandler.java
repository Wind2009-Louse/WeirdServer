package com.weird.handler.chatimpl;

import com.alibaba.fastjson.JSONObject;
import com.weird.facade.BroadcastFacade;
import com.weird.handler.ChatHandler;
import com.weird.model.bo.ChatRoomBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.weird.utils.BroadcastUtil.*;

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

    final static List<String> RECORD_STR_LIST = Arrays.asList("M#", "S#", "T#", "M,OT#");

    final static long TIME_GAP = 1000 * 60 * 90;

    @Override
    public void handle(JSONObject o) {
        String message = o.getString(MESSAGE);
        if (!"group".equals(o.getString(MESSAGE_TYPE))) {
            return;
        }
        String groupId = o.getString(GROUP_ID);
        if (!roomMap.containsKey(groupId)) {
            roomMap.put(groupId, new LinkedList<>());
        }
        List<ChatRoomBO> roomList = roomMap.getOrDefault(groupId, Collections.emptyList());

        for (String recordStr : RECORD_STR_LIST) {
            if (message.contains(recordStr)) {
                String userId = o.getString(QQ);
                String userName = o.getJSONObject("sender").getString("card");
                for (ChatRoomBO data : roomList) {
                    if (data.getUserId().equals(userId)) {
                        data.setChatTime(System.currentTimeMillis());
                        data.setUserName(userName);
                        data.setDetail(message);
                        return;
                    }
                }
                ChatRoomBO chatRoomBO = new ChatRoomBO();
                chatRoomBO.setChatTime(System.currentTimeMillis());
                chatRoomBO.setUserName(userName);
                chatRoomBO.setUserId(userId);
                chatRoomBO.setDetail(message);
                roomList.add(chatRoomBO);
                return;
            }
        }

        if (CALL_STR.equals(message)) {
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
        roomList.sort(Comparator.comparing(ChatRoomBO::getChatTime));
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
