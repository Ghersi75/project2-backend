package com.team2.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team2.backend.Models.User;

public interface UserRepository extends JpaRepository<User,Long>{
    
}
