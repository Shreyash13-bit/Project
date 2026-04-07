//package com.chatterboxx.chatterboxx.dto;
//
//public class CreateRoomRequest {
//
//    private String roomId;
//
//    public String getRoomId() {
//        return roomId;
//    }
//
//    public void setRoomId(String roomId) {
//        this.roomId = roomId;
//    }
//}

package com.chatterboxx.chatterboxx.dto;

public class CreateRoomRequest {

    private String roomId;

    // ✅ NEW: optional — null or empty means public room
    private String password;

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}