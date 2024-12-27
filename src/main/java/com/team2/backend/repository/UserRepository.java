package com.team2.backend.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team2.backend.Models.User;

public interface UserRepository extends JpaRepository<User,Long>{

    Optional<User> findByUsername(String username);
    
}
