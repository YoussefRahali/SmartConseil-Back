package com.example.microserviceuser.Repository;

import com.example.microserviceuser.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);


    Optional<User> findByEmail(String email);
}
