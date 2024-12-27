package com.team2.backend.Controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.team2.backend.Service.ReviewService;
import com.team2.backend.DTO.Review.NewReviewDTO;
import com.team2.backend.Enums.ReviewInteraction;
import com.team2.backend.Models.Review;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class Controller {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/reviews")
    public ResponseEntity<Review> addReview(@RequestBody @Valid NewReviewDTO reviewDTO) {
        Review newReview = reviewService.addReview(reviewDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newReview);
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id); 
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}