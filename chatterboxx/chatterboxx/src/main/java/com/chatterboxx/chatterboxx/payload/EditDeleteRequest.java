package com.chatterboxx.chatterboxx.payload;

public class EditDeleteRequest {

    private String messageId;
    private String requestedBy;  // username making the request — verified server-side

    // For edits only — null when deleting
    private String newContent;

    public EditDeleteRequest() {}

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }

    public String getNewContent() { return newContent; }
    public void setNewContent(String newContent) { this.newContent = newContent; }
}