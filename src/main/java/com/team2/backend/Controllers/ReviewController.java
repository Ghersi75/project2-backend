package com.team2.backend.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.team2.backend.service.ReviewService;
import com.team2.backend.DTO.Review.NewReviewDTO;
import com.team2.backend.DTO.Review.UpdateReviewDTO;
import com.team2.backend.DTO.UserReviewInteraction.UserReviewInteractionDTO;
import com.team2.backend.Enums.ReviewInteraction;
import com.team2.backend.Models.Review;
import com.team2.backend.Kafka.Producer.ReviewInteractionProducer;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewInteractionProducer reviewInteractionProducer;

    @PostMapping("/{userId}")
        public ResponseEntity<Review> addReview(@PathVariable Long userId, @RequestBody @Valid NewReviewDTO reviewDTO) {
        Review newReview = reviewService.addReview(userId, reviewDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newReview);
    }

    @DeleteMapping("/{reviewId}/{userId}")
        public ResponseEntity<Void> deleteReview(@PathVariable Long userId, @PathVariable Long reviewId) {
        reviewService.deleteReview(userId, reviewId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/{reviewId}")
        public ResponseEntity<Review> updateReview(@PathVariable Long userId, @PathVariable Long reviewId, @RequestBody @Valid UpdateReviewDTO updateReviewDTO) {
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

    @GetMapping("/games/{gameId}")
        public ResponseEntity<List<Review>> getAllReviewsByGame(@PathVariable Long gameId) {
        List<Review> reviews = reviewService.getAllReviewsByGame(gameId);
        return ResponseEntity.ok(reviews);
    }
}