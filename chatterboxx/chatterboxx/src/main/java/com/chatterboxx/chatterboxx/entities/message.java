//package com.chatterboxx.chatterboxx.entities;
//
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//
//@Document(collection = "messages")
//public class message {
//
//    @Id
//    private String id;
//
//    private String roomId;
//    private String sender;
//    private String content;
//    private LocalDateTime timestamp;
//    private boolean seen;
//
//    // ✅ NEW: emoji -> set of usernames who reacted
//    // e.g. { "👍": ["alice", "bob"], "❤️": ["carol"] }
//    private Map<String, java.util.Set<String>> reactions = new HashMap<>();
//
//    public message() {}
//
//    public String getId() { return id; }
//    public void setId(String id) { this.id = id; }
//
//    public String getRoomId() { return roomId; }
//    public void setRoomId(String roomId) { this.roomId = roomId; }
//
//    public String getSender() { return sender; }
//    public void setSender(String sender) { this.sender = sender; }
//
//    public String getContent() { return content; }
//    public void setContent(String content) { this.content = content; }
//
//    public LocalDateTime getTimestamp() { return timestamp; }
//    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
//
//    public boolean isSeen() { return seen; }
//    public void setSeen(boolean seen) { this.seen = seen; }
//
//    public Map<String, java.util.Set<String>> getReactions() { return reactions; }
//    public void setReactions(Map<String, java.util.Set<String>> reactions) { this.reactions = reactions; }
//}


package com.chatterboxx.chatterboxx.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Document(collection = "messages")
public class message {

    @Id
    private String id;

    private String roomId;
    private String sender;
    private String content;
    private LocalDateTime timestamp;
    private boolean seen;

    // Reactions: emoji -> set of usernames
    private Map<String, Set<String>> reactions = new HashMap<>();

    // ✅ Soft-delete: message stays in DB, content is hidden in UI
    private boolean deleted = false;

    // ✅ True once the message has been edited
    private boolean edited = false;

    // ✅ Snapshot of sender's avatar at send-time
    private String senderAvatarUrl;

    public message() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public boolean isSeen() { return seen; }
    public void setSeen(boolean seen) { this.seen = seen; }

    public Map<String, Set<String>> getReactions() { return reactions; }
    public void setReactions(Map<String, Set<String>> reactions) { this.reactions = reactions; }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public boolean isEdited() { return edited; }
    public void setEdited(boolean edited) { this.edited = edited; }

    public String getSenderAvatarUrl() { return senderAvatarUrl; }
    public void setSenderAvatarUrl(String senderAvatarUrl) { this.senderAvatarUrl = senderAvatarUrl; }
}