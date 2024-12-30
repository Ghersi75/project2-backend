package com.team2.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team2.backend.Models.UserReviewInteraction;

@Repository
public interface UserReviewInteractionRepository extends JpaRepository<UserReviewInteraction,Long> {

} 