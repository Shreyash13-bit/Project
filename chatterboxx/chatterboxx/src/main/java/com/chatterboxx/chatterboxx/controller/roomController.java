//package com.chatterboxx.chatterboxx.controller;
//
//import com.chatterboxx.chatterboxx.dto.CreateRoomRequest;
//import com.chatterboxx.chatterboxx.entities.room;
//import com.chatterboxx.chatterboxx.entities.message;
//import com.chatterboxx.chatterboxx.repositories.roomRepo;
//import com.chatterboxx.chatterboxx.repositories.MessageRepo;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/rooms")
//@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
//public class roomController {
//
//    private final roomRepo roomRepo;
//    private final MessageRepo messageRepo;
//
//    public roomController(roomRepo roomRepo, MessageRepo messageRepo) {
//        this.roomRepo = roomRepo;
//        this.messageRepo = messageRepo;
//    }
//
//    // ✅ CREATE ROOM
//    @PostMapping
//    public ResponseEntity<?> createRoom(@RequestBody CreateRoomRequest request) {
//
//        String roomId = request.getRoomId().trim();
//
//        if (roomId.isEmpty()) {
//            return ResponseEntity.badRequest().body("Room ID cannot be empty");
//        }
//
//        if (roomRepo.findByRoomId(roomId) != null) {
//            return ResponseEntity.badRequest().body("Room already exists");
//        }
//
//        room room = new room();
//        room.setRoomId(roomId);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(roomRepo.save(room));
//    }
//
//    // ✅ JOIN ROOM
//    @GetMapping("/{roomId}")
//    public ResponseEntity<?> joinRoom(@PathVariable String roomId) {
//
//        room room = roomRepo.findByRoomId(roomId);
//
//        if (room == null) {
//            return ResponseEntity.badRequest().body("Room does not exist");
//        }
//
//        return ResponseEntity.ok(room);
//    }
//
//    // ✅ GET MESSAGES (optimized - separate collection)
//    @GetMapping("/{roomId}/messages")
//    public ResponseEntity<?> getMessages(@PathVariable String roomId) {
//
//        room room = roomRepo.findByRoomId(roomId);
//
//        if (room == null) {
//            return ResponseEntity.badRequest().body("Room not found");
//        }
//
//        List<message> messages = messageRepo.findByRoomIdOrderByTimestampAsc(roomId);
//
//        return ResponseEntity.ok(messages);
//    }
//}


package com.chatterboxx.chatterboxx.controller;

import com.chatterboxx.chatterboxx.dto.CreateRoomRequest;
import com.chatterboxx.chatterboxx.entities.room;
import com.chatterboxx.chatterboxx.entities.message;
import com.chatterboxx.chatterboxx.repositories.roomRepo;
import com.chatterboxx.chatterboxx.repositories.MessageRepo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class roomController {

    private final roomRepo roomRepo;
    private final MessageRepo messageRepo;
    private final PasswordEncoder passwordEncoder;

    public roomController(roomRepo roomRepo,
                          MessageRepo messageRepo,
                          PasswordEncoder passwordEncoder) {
        this.roomRepo = roomRepo;
        this.messageRepo = messageRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // ✅ CREATE ROOM (with optional password)
    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody CreateRoomRequest request) {

        String roomId = request.getRoomId().trim();

        if (roomId.isEmpty()) {
            return ResponseEntity.badRequest().body("Room ID cannot be empty");
        }

        if (roomRepo.findByRoomId(roomId) != null) {
            return ResponseEntity.badRequest().body("Room already exists");
        }

        room room = new room();
        room.setRoomId(roomId);

        // ✅ Hash password if provided
        String rawPassword = request.getPassword();
        if (rawPassword != null && !rawPassword.trim().isEmpty()) {
            room.setPassword(passwordEncoder.encode(rawPassword.trim()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(roomRepo.save(room));
    }

    // ✅ JOIN ROOM (verify password if protected)
    // Accepts optional ?password= query param
    @GetMapping("/{roomId}")
    public ResponseEntity<?> joinRoom(@PathVariable String roomId,
                                      @RequestParam(required = false) String password) {

        room room = roomRepo.findByRoomId(roomId);

        if (room == null) {
            return ResponseEntity.badRequest().body("Room does not exist");
        }

        // ✅ If room has a password, verify it
        if (room.getPassword() != null) {
            if (password == null || password.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Password required");
            }
            if (!passwordEncoder.matches(password.trim(), room.getPassword())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Incorrect password");
            }
        }

        // Don't expose the hashed password to the client
        room.setPassword(null);
        return ResponseEntity.ok(room);
    }

    // ✅ GET MESSAGES
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<?> getMessages(@PathVariable String roomId) {

        room room = roomRepo.findByRoomId(roomId);

        if (room == null) {
            return ResponseEntity.badRequest().body("Room not found");
        }

        List<message> messages = messageRepo.findByRoomIdOrderByTimestampAsc(roomId);

        return ResponseEntity.ok(messages);
    }
}