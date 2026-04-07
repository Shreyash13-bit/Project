package com.chatterboxx.chatterboxx.payload;

public class ReactionRequest {
    private String messageId;
    private String emoji;
    private String username;

    public ReactionRequest() {}

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}