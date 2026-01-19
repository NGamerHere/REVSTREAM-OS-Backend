package com.example.website.service;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.website.dto.LoginRequest;
import com.example.website.entity.Registration;
import com.example.website.repository.RegistrationRepository;

@Service
public class AuthServiceImpl implements AuthService {

    private final RegistrationRepository repository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthServiceImpl(RegistrationRepository repository) {
        this.repository = repository;
    }

    @Override
    public String login(LoginRequest request) {

        Optional<Registration> optionalUser =
                repository.findByCompanyEmail(request.getCompanyEmail());

        if (optionalUser.isEmpty()) {
            return "Invalid email";
        }

        Registration user = optionalUser.get();

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            return "Invalid password";
        }

        return "Login successful";
    }
}
