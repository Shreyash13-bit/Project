package com.chatterboxx.chatterboxx.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@RestController
@RequestMapping("/api/v1/files")
@CrossOrigin(origins = {"http://localhost:5173"})
public class FileController {

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) throws IOException {

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get("uploads/" + fileName);

        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());

        String url = "http://localhost:8080/uploads/" + fileName;

        return ResponseEntity.ok().body(url);
    }
}