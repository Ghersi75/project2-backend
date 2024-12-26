package com.team2.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team2.backend.Models.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long>{
    
    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

}
