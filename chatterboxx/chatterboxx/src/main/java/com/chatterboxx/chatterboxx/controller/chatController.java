//package com.chatterboxx.chatterboxx.controller;
//
//import com.chatterboxx.chatterboxx.entities.message;
//import com.chatterboxx.chatterboxx.payload.MessageRequest;
//import com.chatterboxx.chatterboxx.payload.ReactionRequest;
//import com.chatterboxx.chatterboxx.repositories.MessageRepo;
//import com.chatterboxx.chatterboxx.repositories.roomRepo;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.handler.annotation.*;
//import org.springframework.stereotype.Controller;
//
//import java.time.LocalDateTime;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Controller
//public class chatController {
//
//    @Autowired
//    private roomRepo roomRepo;
//
//    @Autowired
//    private MessageRepo messageRepo;
//
//    private static final ConcurrentHashMap<String, Set<String>> roomUsers = new ConcurrentHashMap<>();
//
//    // ✅ SEND MESSAGE
//    @MessageMapping("/sendMessage/{roomId}")
//    @SendTo("/topic/room/{roomId}")
//    public message sendMessage(
//            @DestinationVariable String roomId,
//            @Payload MessageRequest request
//    ) {
//        message msg = new message();
//        msg.setRoomId(roomId);
//        msg.setContent(request.getContent());
//        msg.setSender(request.getSender());
//        msg.setTimestamp(LocalDateTime.now());
//        msg.setSeen(false);
//
//        return messageRepo.save(msg);
//    }
//
//    // ✅ TYPING
//    @MessageMapping("/typing/{roomId}")
//    @SendTo("/topic/typing/{roomId}")
//    public String typing(@DestinationVariable String roomId,
//                         @Payload String username) {
//        return username;
//    }
//
//    // ✅ USERS JOIN
//    @MessageMapping("/join/{roomId}")
//    @SendTo("/topic/users/{roomId}")
//    public Set<String> join(@DestinationVariable String roomId,
//                            @Payload String username) {
//        roomUsers.putIfAbsent(roomId, new HashSet<>());
//        roomUsers.get(roomId).add(username);
//        return roomUsers.get(roomId);
//    }
//
//    // ✅ USERS LEAVE
//    @MessageMapping("/leave/{roomId}")
//    @SendTo("/topic/users/{roomId}")
//    public Set<String> leave(@DestinationVariable String roomId,
//                             @Payload String username) {
//        if (roomUsers.containsKey(roomId)) {
//            roomUsers.get(roomId).remove(username);
//        }
//        return roomUsers.getOrDefault(roomId, new HashSet<>());
//    }
//
//    // ✅ READ RECEIPT
//    @MessageMapping("/seen/{roomId}")
//    @SendTo("/topic/seen/{roomId}")
//    public String markSeen(@DestinationVariable String roomId,
//                           @Payload String messageId) {
//        Optional<message> msg = messageRepo.findById(messageId);
//        if (msg.isPresent()) {
//            msg.get().setSeen(true);
//            messageRepo.save(msg.get());
//        }
//        return messageId;
//    }
//
//    // ✅ PRIVATE MESSAGE
//    @MessageMapping("/private")
//    @SendTo("/topic/private")
//    public message privateMessage(@Payload message msg) {
//        msg.setTimestamp(LocalDateTime.now());
//        msg.setSeen(false);
//        return messageRepo.save(msg);
//    }
//
//    // ✅ NEW: REACT TO MESSAGE
//    // Frontend sends: { messageId, emoji, username }
//    // Backend toggles the reaction (add if not present, remove if already reacted)
//    // Broadcasts the updated message back to the room
//    @MessageMapping("/react/{roomId}")
//    @SendTo("/topic/reactions/{roomId}")
//    public message reactToMessage(
//            @DestinationVariable String roomId,
//            @Payload ReactionRequest request
//    ) {
//        Optional<message> optional = messageRepo.findById(request.getMessageId());
//        if (optional.isEmpty()) return null;
//
//        message msg = optional.get();
//        Map<String, Set<String>> reactions = msg.getReactions();
//        if (reactions == null) {
//            reactions = new java.util.HashMap<>();
//        }
//
//        String emoji = request.getEmoji();
//        String username = request.getUsername();
//
//        reactions.putIfAbsent(emoji, new HashSet<>());
//        Set<String> users = reactions.get(emoji);
//
//        // Toggle: remove if already reacted, add if not
//        if (users.contains(username)) {
//            users.remove(username);
//            if (users.isEmpty()) reactions.remove(emoji);
//        } else {
//            users.add(username);
//        }
//
//        msg.setReactions(reactions);
//        return messageRepo.save(msg);
//    }
//}


package com.chatterboxx.chatterboxx.controller;

import com.chatterboxx.chatterboxx.entities.User;
import com.chatterboxx.chatterboxx.entities.message;
import com.chatterboxx.chatterboxx.payload.EditDeleteRequest;
import com.chatterboxx.chatterboxx.payload.MessageRequest;
import com.chatterboxx.chatterboxx.payload.ReactionRequest;
import com.chatterboxx.chatterboxx.repositories.MessageRepo;
import com.chatterboxx.chatterboxx.repositories.UserRepo;
import com.chatterboxx.chatterboxx.repositories.roomRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class chatController {

    @Autowired private roomRepo roomRepo;
    @Autowired private MessageRepo messageRepo;
    @Autowired private UserRepo userRepo;

    private static final ConcurrentHashMap<String, Set<String>> roomUsers = new ConcurrentHashMap<>();

    // ✅ SEND MESSAGE — attaches sender's current avatarUrl to the message
    @MessageMapping("/sendMessage/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public message sendMessage(
            @DestinationVariable String roomId,
            @Payload MessageRequest request
    ) {
        message msg = new message();
        msg.setRoomId(roomId);
        msg.setContent(request.getContent());
        msg.setSender(request.getSender());
        msg.setTimestamp(LocalDateTime.now());
        msg.setSeen(false);

        // ✅ Snapshot avatar at send time
        userRepo.findByUsername(request.getSender())
                .ifPresent(u -> msg.setSenderAvatarUrl(u.getAvatarUrl()));

        return messageRepo.save(msg);
    }

    // ✅ TYPING
    @MessageMapping("/typing/{roomId}")
    @SendTo("/topic/typing/{roomId}")
    public String typing(@DestinationVariable String roomId, @Payload String username) {
        return username;
    }

    // ✅ JOIN
    @MessageMapping("/join/{roomId}")
    @SendTo("/topic/users/{roomId}")
    public Set<String> join(@DestinationVariable String roomId, @Payload String username) {
        roomUsers.putIfAbsent(roomId, new HashSet<>());
        roomUsers.get(roomId).add(username);
        return roomUsers.get(roomId);
    }

    // ✅ LEAVE
    @MessageMapping("/leave/{roomId}")
    @SendTo("/topic/users/{roomId}")
    public Set<String> leave(@DestinationVariable String roomId, @Payload String username) {
        if (roomUsers.containsKey(roomId)) roomUsers.get(roomId).remove(username);
        return roomUsers.getOrDefault(roomId, new HashSet<>());
    }

    // ✅ READ RECEIPT
    @MessageMapping("/seen/{roomId}")
    @SendTo("/topic/seen/{roomId}")
    public String markSeen(@DestinationVariable String roomId, @Payload String messageId) {
        messageRepo.findById(messageId).ifPresent(m -> {
            m.setSeen(true);
            messageRepo.save(m);
        });
        return messageId;
    }

    // ✅ PRIVATE MESSAGE
    @MessageMapping("/private")
    @SendTo("/topic/private")
    public message privateMessage(@Payload message msg) {
        msg.setTimestamp(LocalDateTime.now());
        msg.setSeen(false);
        return messageRepo.save(msg);
    }

    // ✅ REACT TO MESSAGE
    @MessageMapping("/react/{roomId}")
    @SendTo("/topic/reactions/{roomId}")
    public message reactToMessage(
            @DestinationVariable String roomId,
            @Payload ReactionRequest request
    ) {
        Optional<message> optional = messageRepo.findById(request.getMessageId());
        if (optional.isEmpty()) return null;

        message msg = optional.get();
        Map<String, Set<String>> reactions = msg.getReactions();
        if (reactions == null) reactions = new HashMap<>();

        String emoji = request.getEmoji();
        String username = request.getUsername();

        reactions.putIfAbsent(emoji, new HashSet<>());
        Set<String> users = reactions.get(emoji);

        if (users.contains(username)) {
            users.remove(username);
            if (users.isEmpty()) reactions.remove(emoji);
        } else {
            users.add(username);
        }

        msg.setReactions(reactions);
        return messageRepo.save(msg);
    }

    // ✅ NEW: EDIT MESSAGE
    // Only the original sender can edit — enforced by checking requestedBy == sender
    // Broadcasts the updated message to /topic/updates/{roomId}
    @MessageMapping("/edit/{roomId}")
    @SendTo("/topic/updates/{roomId}")
    public message editMessage(
            @DestinationVariable String roomId,
            @Payload EditDeleteRequest request
    ) {
        Optional<message> optional = messageRepo.findById(request.getMessageId());
        if (optional.isEmpty()) return null;

        message msg = optional.get();

        // Security check: only the original sender may edit
        if (!msg.getSender().equals(request.getRequestedBy())) return null;

        // Don't allow editing deleted messages
        if (msg.isDeleted()) return null;

        String newContent = request.getNewContent();
        if (newContent == null || newContent.trim().isEmpty()) return null;

        msg.setContent(newContent.trim());
        msg.setEdited(true);
        return messageRepo.save(msg);
    }

    // ✅ NEW: DELETE MESSAGE (soft-delete)
    // Only the original sender can delete
    // Broadcasts the updated message so every client hides the content
    @MessageMapping("/delete/{roomId}")
    @SendTo("/topic/updates/{roomId}")
    public message deleteMessage(
            @DestinationVariable String roomId,
            @Payload EditDeleteRequest request
    ) {
        Optional<message> optional = messageRepo.findById(request.getMessageId());
        if (optional.isEmpty()) return null;

        message msg = optional.get();

        // Security check: only the original sender may delete
        if (!msg.getSender().equals(request.getRequestedBy())) return null;

        msg.setDeleted(true);
        msg.setContent("[This message was deleted]"); // fallback for any raw DB reads
        return messageRepo.save(msg);
    }
}