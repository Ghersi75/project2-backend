package com.team2.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.team2.backend.service.ReviewService;
import com.team2.backend.dto.review.NewReviewDTO;
import com.team2.backend.dto.review.UpdateReviewDTO;
import com.team2.backend.dto.userreviewinteraction.UserReviewInteractionDTO;
import com.team2.backend.enums.ReviewInteraction;
import com.team2.backend.exceptions.Status401Exception;
import com.team2.backend.kafka.Producer.ReviewInteractionProducer;
import com.team2.backend.models.Review;

import java.util.List;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewInteractionProducer reviewInteractionProducer;

    @PostMapping("/{username}")
    public ResponseEntity<Review> addReview(@PathVariable String username, @RequestBody @Valid NewReviewDTO reviewDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // If jwt username and given username don't match, throw exception
        // Can't create leave a review for someone else regardless of permissions
        if (auth.getName() != username) {
            // TODO: Create custom exception if there's time
            throw new Status401Exception("Can't create a review for another user");
        }

        Review newReview = reviewService.addReview(username, reviewDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newReview);
    }

    @DeleteMapping("/{reviewId}/{userId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long userId, @PathVariable Long reviewId) {
        reviewService.deleteReview(userId, reviewId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/{reviewId}")
    public ResponseEntity<Review> updateReview(@PathVariable Long userId, @PathVariable Long reviewId,
            @RequestBody @Valid UpdateReviewDTO updateReviewDTO) {
        Review updatedReview = reviewService.updateReview(userId, reviewId, updateReviewDTO);
        return ResponseEntity.ok(updatedReview);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Review>> getAllReviewsByUser(@PathVariable Long userId) {
        List<Review> reviews = reviewService.getAllReviewsByUser(userId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/like")
    public ResponseEntity<String> likeReview(@RequestParam(name = "userId") Long userId,
            @RequestBody UserReviewInteractionDTO interactionDTO) {
        reviewService.likeOrDislikeReview(userId, interactionDTO);

        interactionDTO.setInteraction(ReviewInteraction.LIKE);
        reviewInteractionProducer.sendReviewInteraction(interactionDTO);
        return ResponseEntity.ok("Review liked successfully");
    }

    @PostMapping("/dislike")
    public ResponseEntity<String> dislikeReview(@RequestParam(name = "userId") Long userId,
            @RequestBody UserReviewInteractionDTO interactionDTO) {

        reviewService.likeOrDislikeReview(userId, interactionDTO);

        interactionDTO.setInteraction(ReviewInteraction.DISLIKE);
        reviewInteractionProducer.sendReviewInteraction(interactionDTO);
        return ResponseEntity.ok("Review disliked successfully");
    }

    @GetMapping("/games/{appid}")
    public ResponseEntity<List<Review>> getAllReviewsByGame(@PathVariable Integer appid) {
        List<Review> reviews = reviewService.getAllReviewsByGame(appid);
        return ResponseEntity.ok(reviews);
    }

}
