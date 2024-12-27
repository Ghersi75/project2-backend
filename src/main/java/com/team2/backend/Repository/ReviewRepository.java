package com.team2.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team2.backend.Models.Review;

public interface ReviewRepository extends JpaRepository<Review,Long>{
    
}
