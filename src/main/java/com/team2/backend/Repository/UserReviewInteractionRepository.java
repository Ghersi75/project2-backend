package com.team2.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team2.backend.Models.User;
import com.team2.backend.Models.Review;
import com.team2.backend.Models.UserReviewInteraction;

import java.util.Optional;

public interface UserReviewInteractionRepository extends JpaRepository<UserReviewInteraction,Long> {

    Optional<UserReviewInteraction> findByUserAndReview(User user, Review review);
} 