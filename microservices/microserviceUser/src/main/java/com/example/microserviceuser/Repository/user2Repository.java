package com.example.microserviceuser.Repository;

import com.example.microserviceuser.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface user2Repository extends JpaRepository<User,Long> {


    User findByEmail(String email);
}
