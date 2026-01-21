package com.example.website.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.website.dto.LoginRequest;
import com.example.website.entity.Registration;
import com.example.website.jwt.JwtUtil;
import com.example.website.repository.RegistrationRepository;

@Service
public class AuthServiceImpl implements AuthService {

    private final RegistrationRepository registrationRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(RegistrationRepository registrationRepository, JwtUtil jwtUtil) {
        this.registrationRepository = registrationRepository;
        this.jwtUtil = jwtUtil;
        this.encoder = new BCryptPasswordEncoder();
    }

    @Override
    public String login(LoginRequest request) {

        Registration reg = registrationRepository.findByCompanyEmail(request.getCompanyEmail())
                .orElse(null);

        if (reg == null) {
            return null; // user not found
        }

        if (!encoder.matches(request.getPassword(), reg.getPassword())) {
            return null; // wrong password
        }

        //  generate JWT token
        return jwtUtil.generateToken(reg.getCompanyEmail());
    }
}
