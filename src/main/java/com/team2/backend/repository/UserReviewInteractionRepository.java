package com.team2.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team2.backend.models.UserReviewInteraction;

@Repository
public interface UserReviewInteractionRepository extends JpaRepository<UserReviewInteraction, Long> {

}