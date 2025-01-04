package com.team2.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team2.backend.Models.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long>{
    
}
