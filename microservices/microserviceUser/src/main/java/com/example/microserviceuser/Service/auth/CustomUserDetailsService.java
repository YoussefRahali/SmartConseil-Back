package com.example.microserviceuser.Service.auth;

import com.example.microserviceuser.Entity.User;
import com.example.microserviceuser.Repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        System.out.println("Loading user: " + user.getEmail() + " with role: " + user.getRole());

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().toUpperCase().replace(" ", "_"))
                .build();
    }

    public Long getUserIdByUsername(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return user.getId();
        }
        return null;
    }

    public String getUserusername(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return user.getUsername();
        }
        return null;
    }

    public String getUserRoleByUsername(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return user.getRole();
        }
        return null;
    }

}
