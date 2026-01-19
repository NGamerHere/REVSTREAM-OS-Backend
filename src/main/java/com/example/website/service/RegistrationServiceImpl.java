package com.example.website.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.website.dto.RegistrationRequest;
import com.example.website.dto.RegistrationResponse;
import com.example.website.entity.Registration;
import com.example.website.repository.RegistrationRepository;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    @Autowired
    private RegistrationRepository repository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public RegistrationResponse register(RegistrationRequest request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return new RegistrationResponse(null, "Passwords do not match");
        }

        if (repository.existsByCompanyEmail(request.getCompanyEmail())) {
            return new RegistrationResponse(null, "Email already registered");
        }

        Registration reg = new Registration();
        reg.setCompanyName(request.getCompanyName());
        reg.setCompanyEmail(request.getCompanyEmail());
        reg.setCompanySize(request.getCompanySize());
        reg.setPrimaryStack(request.getPrimaryStack());
        reg.setPassword(encoder.encode(request.getPassword()));

        Registration saved = repository.save(reg);

        return new RegistrationResponse(saved.getId(), "Registration successful");
    }
}
