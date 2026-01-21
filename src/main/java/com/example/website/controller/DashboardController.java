package com.example.website.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

    @GetMapping("/me")
    public String me(Authentication authentication) {

        String email = authentication.getName();
        Long userId = (Long) authentication.getCredentials();

        return "userId = " + userId + ", email = " + email;
    }
}
