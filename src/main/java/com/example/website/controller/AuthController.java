package com.example.website.controller;

import com.example.website.dto.LoginRequest;
import com.example.website.dto.LoginResponse;
import com.example.website.entity.Registration;
import com.example.website.jwt.JwtUtil;
import com.example.website.repository.RegistrationRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RegistrationRepository registrationRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil,RegistrationRepository registrationRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.registrationRepository = registrationRepository;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getCompanyEmail(),
                        request.getPassword()
                )
        );

        Registration user = registrationRepository.findByCompanyEmail(request.getCompanyEmail()).orElseThrow();
        Long userId = user.getId();
        String token = jwtUtil.generateToken(userId, request.getCompanyEmail());

        return new LoginResponse(token, "Login successful");
    }
}
