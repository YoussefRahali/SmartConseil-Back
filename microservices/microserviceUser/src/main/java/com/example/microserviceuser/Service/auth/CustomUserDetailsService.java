package com.example.microserviceuser.Service.auth;

import com.example.microserviceuser.Entity.User;
import com.example.microserviceuser.Repository.UserRepository;
import com.example.microserviceuser.Repository.user2Repository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final user2Repository userRepository2;
    public CustomUserDetailsService(UserRepository userRepository, user2Repository userRepository2) {
        this.userRepository = userRepository;
        this.userRepository2 = userRepository2;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository2.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }

    public Long getUserIdByUsername(String email) {
        User user = userRepository2.findByEmail(email);  // Récupérer l'utilisateur depuis la base de données
        if (user != null) {
            return user.getId();  // Retourner l'ID de l'utilisateur
        }
        return null;
    }
    public String getUserusername(String email) {
        User user = userRepository2.findByEmail(email);
        if (user != null) {
            return user.getUsername();
        }
        return null;
    }
    public String getUserRoleByUsername(String username) {
        User user = userRepository2.findByEmail(username);  // Récupérer l'utilisateur depuis la base de données
        if (user != null) {
            return user.getRole();  // Retourner l'ID de l'utilisateur
        }
        return null;
    }

}
