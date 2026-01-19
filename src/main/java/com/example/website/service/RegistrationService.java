package com.example.website.service;

import com.example.website.dto.RegistrationRequest;
import com.example.website.dto.RegistrationResponse;

public interface RegistrationService {
    RegistrationResponse register(RegistrationRequest request);
}
