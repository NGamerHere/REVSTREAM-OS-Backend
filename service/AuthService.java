package com.example.website.service;

import com.example.website.dto.LoginRequest;

public interface AuthService {

    String login(LoginRequest request);
}
