package com.example.website.security.customer;

import com.example.website.entity.Registration;
import com.example.website.repository.RegistrationRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final RegistrationRepository registrationRepository;

    public CustomUserDetailsService(RegistrationRepository registrationRepository) {
        this.registrationRepository = registrationRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Registration reg = registrationRepository.findByCompanyEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.withUsername(reg.getCompanyEmail())
                .password(reg.getPassword())
                .roles("USER")
                .build();
    }
}
