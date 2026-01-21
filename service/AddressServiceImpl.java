package com.example.website.service;

import org.springframework.stereotype.Service;

import com.example.website.dto.AddressRequest;
import com.example.website.entity.Address;
import com.example.website.entity.Registration;
import com.example.website.repository.AddressRepository;
import com.example.website.repository.RegistrationRepository;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final RegistrationRepository registrationRepository;

    public AddressServiceImpl(AddressRepository addressRepository,
                              RegistrationRepository registrationRepository) {
        this.addressRepository = addressRepository;
        this.registrationRepository = registrationRepository;
    }

    @Override
    public String saveAddress(Long registrationId, AddressRequest request) {

        Registration reg = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Registration not found with ID: " + registrationId));

        // ✅ avoid duplicate address for same registration
        if (addressRepository.existsByRegistrationId(registrationId)) {
            return "Address already added for this registration";
        }

        Address address = new Address();
        address.setBuildingNameFloor(request.getBuildingNameFloor());
        address.setStreetNameLaneRoadNo(request.getStreetNameLaneRoadNo());
        address.setCityTown(request.getCityTown());
        address.setDistrict(request.getDistrict());
        address.setPinCode(request.getPinCode());
        address.setCountry(request.getCountry());

        address.setRegistration(reg);

        addressRepository.save(address);

        return "Address saved successfully";
    }
}
