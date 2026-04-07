package com.chatterboxx.chatterboxx.payload;

import java.time.LocalDateTime;

public class MessageRequest {

    private String content;
    private String sender;
    private String roomId;
    private LocalDateTime timestamp;

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }
    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRoomId() {
        return roomId;
    }
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public MessageRequest(String content, String sender, String roomId) {
        this.content = content;
        this.sender = sender;
        this.roomId = roomId;
        this.timestamp = LocalDateTime.now();
    }

    public MessageRequest() {
    }

}
