package com.example.website.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.website.dto.LoginRequest;
import com.example.website.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {

        String result = authService.login(request);

        if ("Login successful".equals(result)) {
            return ResponseEntity.ok(result); // ✅ 200 OK
        } else {
            return ResponseEntity.status(401).body(result); // ✅ 401 Unauthorized
        }
    }
}
