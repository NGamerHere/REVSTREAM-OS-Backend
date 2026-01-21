package com.example.website.service;

import com.example.website.dto.AddressRequest;

public interface AddressService {

    String saveAddress(Long registrationId, AddressRequest request);
}
