package com.team2.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team2.backend.models.Review;
import com.team2.backend.models.User;
import com.team2.backend.models.UserReviewInteraction;

import java.util.Optional;

@Repository
public interface UserReviewInteractionRepository extends JpaRepository<UserReviewInteraction,Long> {
    Optional<UserReviewInteraction> findByUseridAndReview(Long userid, Review review);
} 