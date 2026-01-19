package com.example.website.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.website.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

    boolean existsByRegistrationId(Long registrationId);
}
