//package com.chatterboxx.chatterboxx.dto;
//
//public class AuthResponse {
//    private String token;
//    private String username;
//
//    public AuthResponse(String token, String username) {
//        this.token = token;
//        this.username = username;
//    }
//
//    public String getToken() { return token; }
//    public String getUsername() { return username; }
//}


package com.chatterboxx.chatterboxx.dto;

public class AuthResponse {
    private String token;
    private String username;

    // ✅ NEW: returned on login/register so frontend can show avatar immediately
    private String avatarUrl;

    public AuthResponse(String token, String username, String avatarUrl) {
        this.token = token;
        this.username = username;
        this.avatarUrl = avatarUrl;
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
    public String getAvatarUrl() { return avatarUrl; }
}