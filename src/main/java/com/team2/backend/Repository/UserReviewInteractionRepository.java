package com.team2.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team2.backend.Models.User;
import com.team2.backend.Models.Review;
import com.team2.backend.Models.UserReviewInteraction;

import java.util.Optional;

@Repository
public interface UserReviewInteractionRepository extends JpaRepository<UserReviewInteraction,Long> {
    Optional<UserReviewInteraction> findByUseridAndReview(Long userid, Review review);
} 