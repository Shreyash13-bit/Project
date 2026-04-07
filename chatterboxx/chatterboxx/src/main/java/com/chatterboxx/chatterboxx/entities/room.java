//package com.chatterboxx.chatterboxx.entities;
//
//
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//
//import java.util.AbstractList;
//import java.util.ArrayList;
//import java.util.List;
////
////@Getter
////@Setter
////@NoArgsConstructor
////@AllArgsConstructor
////@Document(collection = "rooms")
////public class room {
////
////    @Id
////    private String id; //MongoDb unique identifier
////    private String roomId;
////
////    private List<message> messages = new ArrayList<>();
////
////}
//
//
//@Document(collection = "rooms")
//public class room {
//
//    @Id
//    private String id;
//    private String roomId;
//    private List<message> messages = new ArrayList<>();
//
//    public room() {}
//
//    public String getId() { return id; }
//    public void setId(String id) { this.id = id; }
//
//    public String getRoomId() { return roomId; }
//    public void setRoomId(String roomId) { this.roomId = roomId; }
//
//    public List<message> getMessages() { return messages; }
//    public void setMessages(List<message> messages) { this.messages = messages; }
//}


package com.chatterboxx.chatterboxx.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "rooms")
public class room {

    @Id
    private String id;
    private String roomId;
    private List<message> messages = new ArrayList<>();

    // ✅ NEW: bcrypt-hashed password, null = public room
    private String password;

    public room() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public List<message> getMessages() { return messages; }
    public void setMessages(List<message> messages) { this.messages = messages; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}