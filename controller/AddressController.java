package com.example.website.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.website.dto.AddressRequest;
import com.example.website.service.AddressService;

@RestController
@RequestMapping("/api/address")
@CrossOrigin("*")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    //  Step-2 API
    @PostMapping("/{registrationId}")
    public ResponseEntity<String> saveAddress(@PathVariable Long registrationId,
                                              @RequestBody AddressRequest request) {
        return ResponseEntity.ok(addressService.saveAddress(registrationId, request));
    }
    
    
}
