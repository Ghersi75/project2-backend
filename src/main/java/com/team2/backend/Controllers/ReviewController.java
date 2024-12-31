package com.team2.backend.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.team2.backend.service.ReviewService;
import com.team2.backend.DTO.Review.NewReviewDTO;
import com.team2.backend.DTO.UserReviewInteraction.UserReviewInteractionDTO;
import com.team2.backend.Enums.ReviewInteraction;
import com.team2.backend.Models.Review;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/{userId}")
public ResponseEntity<Review> addReview(
    @PathVariable Long userId, 
    @RequestBody @Valid NewReviewDTO reviewDTO) {
    Review newReview = reviewService.addReview(userId, reviewDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(newReview);
}

@DeleteMapping("/{reviewId}/{userId}")
public ResponseEntity<Void> deleteReview(
    @PathVariable Long userId,
    @PathVariable Long reviewId) {
    reviewService.deleteReview(userId, reviewId);
    return ResponseEntity.noContent().build();
}

    @PostMapping("/like")
    public ResponseEntity<String> likeReview(@RequestParam(name = "userId") Long userId,
                                         @RequestBody UserReviewInteractionDTO interactionDTO) {
    reviewService.likeOrDislikeReview(userId, interactionDTO);
    return ResponseEntity.ok("Review liked successfully");
    }

    @PostMapping("/dislike")
    public ResponseEntity<String> dislikeReview(@RequestParam(name = "userId") Long userId,
                                            @RequestBody UserReviewInteractionDTO interactionDTO) {
    reviewService.likeOrDislikeReview(userId, interactionDTO);
    return ResponseEntity.ok("Review disliked successfully");
    }
}