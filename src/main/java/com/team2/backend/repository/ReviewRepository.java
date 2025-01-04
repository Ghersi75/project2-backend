package com.team2.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team2.backend.models.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

}
