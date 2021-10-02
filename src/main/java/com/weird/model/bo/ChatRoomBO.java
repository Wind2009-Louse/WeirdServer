package com.weird.model.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 消息记录中的房间信息
 *
 * @author Nidhogg
 * @date 2021.10.2
 */
@Data
public class ChatRoomBO implements Serializable {
    /**
     * 消息时间
     */
    long chatTime;

    /**
     * 消息发送者
     */
    String userName;

    /**
     * 消息内容
     */
    String detail;
}
