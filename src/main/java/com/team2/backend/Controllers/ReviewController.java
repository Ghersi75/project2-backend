package com.team2.backend.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team2.backend.DTO.Review.NewReviewDTO;
import com.team2.backend.Models.Review;
import com.team2.backend.Service.ReviewService;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
     @Autowired
    private ReviewService reviewService;

    // Endpoint to create a review
    @PostMapping("/create")
    public ResponseEntity<String> createReview(@RequestBody NewReviewDTO reviewDTO) {
            reviewService.createReview(reviewDTO);
            return ResponseEntity.status(HttpStatus.OK).body("Review created successfully.");
    }

    // Endpoint to delete a review by ID
    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId) {
            reviewService.deleteReview(reviewId);
            return ResponseEntity.ok("Review deleted successfully.");
    }
    
}
