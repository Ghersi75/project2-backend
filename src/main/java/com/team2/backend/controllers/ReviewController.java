package com.team2.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.team2.backend.service.ReviewService;
import com.team2.backend.dto.review.NewReviewDTO;
import com.team2.backend.dto.review.ReviewDTO;
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
    public ReviewDTO addReview(@PathVariable String username, @RequestBody @Valid NewReviewDTO reviewDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // If jwt username and given username don't match, throw exception
        // Can't create leave a review for someone else regardless of permissions
        System.out.println(auth.getName());
        System.out.println(username);
        if (!auth.getName().equalsIgnoreCase(username)) {
            // TODO: Create custom exception if there's time
            throw new Status401Exception("Can't create a review for another user");
        }

        return reviewService.addReview(username, reviewDTO);
    }

    @DeleteMapping("/{username}/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable String username, @PathVariable Long reviewId) {
        reviewService.deleteReview(username, reviewId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{username}/{reviewId}")
    public ResponseEntity<Review> updateReview(@PathVariable String username, @PathVariable Long reviewId,
            @RequestBody @Valid UpdateReviewDTO updateReviewDTO) {
        Review updatedReview = reviewService.updateReview(username, reviewId, updateReviewDTO);
        return ResponseEntity.ok(updatedReview);
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<Review>> getAllReviewsByUser(@PathVariable String username) {
        List<Review> reviews = reviewService.getAllReviewsByUser(username);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/like")
    public ResponseEntity<String> likeReview(@RequestParam(name = "username") String username,
            @RequestBody UserReviewInteractionDTO interactionDTO) {
        reviewService.likeOrDislikeReview(username, interactionDTO);

        interactionDTO.setInteraction(ReviewInteraction.LIKE);
        reviewInteractionProducer.sendReviewInteraction(interactionDTO);
        return ResponseEntity.ok("Review liked successfully");
    }

    @PostMapping("/dislike")
    public ResponseEntity<String> dislikeReview(@RequestParam(name = "username") String username,
            @RequestBody UserReviewInteractionDTO interactionDTO) {

        reviewService.likeOrDislikeReview(username, interactionDTO);

        interactionDTO.setInteraction(ReviewInteraction.DISLIKE);
        reviewInteractionProducer.sendReviewInteraction(interactionDTO);
        return ResponseEntity.ok("Review disliked successfully");
    }

    @GetMapping("/games/{appid}")
    public List<ReviewDTO> getAllReviewsByGame(@PathVariable Integer appid) {
        return reviewService.getAllReviewsByGame(appid);
    }

}
