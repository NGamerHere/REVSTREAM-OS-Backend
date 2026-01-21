package com.example.website.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.website.entity.Registration;

public interface RegistrationRepository 
        extends JpaRepository<Registration, Long> {

    boolean existsByCompanyEmail(String companyEmail);

    Optional<Registration> findByCompanyEmail(String companyEmail);
}
