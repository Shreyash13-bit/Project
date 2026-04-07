//package com.chatterboxx.chatterboxx.controller;
//
//import com.chatterboxx.chatterboxx.config.JwtUtil;
//import com.chatterboxx.chatterboxx.dto.AuthResponse;
//import com.chatterboxx.chatterboxx.dto.LoginRequest;
//import com.chatterboxx.chatterboxx.dto.RegisterRequest;
//import com.chatterboxx.chatterboxx.entities.User;
//import com.chatterboxx.chatterboxx.repositories.UserRepo;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/v1/auth")
//@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
//public class AuthController {
//
//    private final UserRepo userRepo;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtUtil jwtUtil;
//
//    public AuthController(UserRepo userRepo,
//                          PasswordEncoder passwordEncoder,
//                          JwtUtil jwtUtil) {
//        this.userRepo = userRepo;
//        this.passwordEncoder = passwordEncoder;
//        this.jwtUtil = jwtUtil;
//    }
//
//    // ✅ Register
//    @PostMapping("/register")
//    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
//
//        String username = request.getUsername().trim();
//        String password = request.getPassword().trim();
//
//        if (username.isEmpty() || password.isEmpty()) {
//            return ResponseEntity.badRequest().body("Username and password are required");
//        }
//
//        if (password.length() < 6) {
//            return ResponseEntity.badRequest().body("Password must be at least 6 characters");
//        }
//
//        if (userRepo.existsByUsername(username)) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken");
//        }
//
//        User user = new User();
//        user.setUsername(username);
//        user.setPassword(passwordEncoder.encode(password));
//        userRepo.save(user);
//
//        String token = jwtUtil.generateToken(username);
//        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token, username));
//    }
//
//    // ✅ Login
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
//
//        String username = request.getUsername().trim();
//        String password = request.getPassword().trim();
//
//        User user = userRepo.findByUsername(username)
//                .orElse(null);
//
//        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
//        }
//
//        String token = jwtUtil.generateToken(username);
//        return ResponseEntity.ok(new AuthResponse(token, username));
//    }
//
//    // ✅ Validate token (used by frontend on refresh)
//    @GetMapping("/validate")
//    public ResponseEntity<?> validate() {
//        // If this endpoint is reached, JwtFilter already validated the token
//        return ResponseEntity.ok("Token is valid");
//    }
//}


package com.chatterboxx.chatterboxx.controller;

import com.chatterboxx.chatterboxx.config.JwtUtil;
import com.chatterboxx.chatterboxx.dto.AuthResponse;
import com.chatterboxx.chatterboxx.dto.LoginRequest;
import com.chatterboxx.chatterboxx.dto.RegisterRequest;
import com.chatterboxx.chatterboxx.entities.User;
import com.chatterboxx.chatterboxx.repositories.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AuthController {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepo userRepo,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // ✅ Register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        String username = request.getUsername().trim();
        String password = request.getPassword().trim();

        if (username.isEmpty() || password.isEmpty())
            return ResponseEntity.badRequest().body("Username and password are required");

        if (password.length() < 6)
            return ResponseEntity.badRequest().body("Password must be at least 6 characters");

        if (userRepo.existsByUsername(username))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken");

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        userRepo.save(user);

        String token = jwtUtil.generateToken(username);
        // ✅ avatarUrl is null on fresh register — frontend uses initials fallback
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(token, username, null));
    }

    // ✅ Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String username = request.getUsername().trim();
        String password = request.getPassword().trim();

        User user = userRepo.findByUsername(username).orElse(null);

        if (user == null || !passwordEncoder.matches(password, user.getPassword()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");

        String token = jwtUtil.generateToken(username);
        // ✅ Return existing avatarUrl if the user already has one
        return ResponseEntity.ok(new AuthResponse(token, username, user.getAvatarUrl()));
    }

    // ✅ Validate token
    @GetMapping("/validate")
    public ResponseEntity<?> validate() {
        return ResponseEntity.ok("Token is valid");
    }

    // ✅ NEW: Upload or replace avatar for the authenticated user
    // Endpoint: POST /api/v1/auth/avatar  (multipart/form-data, field name = "file")
    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {

        // Get the authenticated username from the JWT (set by JwtFilter)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) auth.getPrincipal();

        User user = userRepo.findByUsername(username).orElse(null);
        if (user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

        // Save file to disk
        String fileName = System.currentTimeMillis() + "_" + username + "_avatar."
                + getExtension(file.getOriginalFilename());
        Path path = Paths.get("uploads/" + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());

        String avatarUrl = "http://localhost:8080/uploads/" + fileName;
        user.setAvatarUrl(avatarUrl);
        userRepo.save(user);

        return ResponseEntity.ok(avatarUrl);
    }

    // ✅ NEW: Get current user's profile (used by frontend to refresh avatar after upload)
    @GetMapping("/me")
    public ResponseEntity<?> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) auth.getPrincipal();

        User user = userRepo.findByUsername(username).orElse(null);
        if (user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

        return ResponseEntity.ok(new AuthResponse(
                null, // don't re-issue token on profile fetch
                user.getUsername(),
                user.getAvatarUrl()
        ));
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "png";
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}