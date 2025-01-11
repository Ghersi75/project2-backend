package com.team2.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team2.backend.dto.review.NewReviewDTO;
import com.team2.backend.dto.review.ReviewDTO;
import com.team2.backend.dto.review.ReviewWithLikedDTO;
import com.team2.backend.dto.review.UpdateReviewDTO;
import com.team2.backend.dto.userreviewinteraction.ProducerInteractionDTO;
import com.team2.backend.dto.userreviewinteraction.UserInteractionResultDTO;
import com.team2.backend.dto.userreviewinteraction.UserReviewInteractionDTO;
import com.team2.backend.enums.NotificationType;
import com.team2.backend.enums.ReviewInteraction;
import com.team2.backend.exceptions.Status401Exception;
import com.team2.backend.kafka.Producer.ReviewCreationProducer;
import com.team2.backend.kafka.Producer.ReviewInteractionProducer;
import com.team2.backend.models.Review;
import com.team2.backend.service.ReviewService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewInteractionProducer reviewInteractionProducer;

    @Autowired
    private ReviewCreationProducer reviewCreationProducer;

    // Done
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

        ReviewDTO newReview = reviewService.addReview(username, reviewDTO);
        ProducerInteractionDTO producerInteractionDTO = new ProducerInteractionDTO(username,
                newReview.getReviewId(), reviewDTO.getAppid(), NotificationType.REVIEW);

        reviewCreationProducer.sendReviewCreation(producerInteractionDTO);
        return newReview;

    }

    // Done
    @DeleteMapping("/{username}/{reviewId}")
    public void deleteReview(@PathVariable String username, @PathVariable Long reviewId) {
        reviewService.deleteReview(username, reviewId);
    }

    // Done
    @PutMapping("/{username}/{reviewId}")
    public void updateReview(@PathVariable String username, @PathVariable Long reviewId,
            @RequestBody @Valid UpdateReviewDTO updateReviewDTO) {

        reviewService.updateReview(username, reviewId, updateReviewDTO);
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<Review>> getAllReviewsByUser(@PathVariable String username) {
        List<Review> reviews = reviewService.getAllReviewsByUser(username);
        return ResponseEntity.ok(reviews);
    }

    // Done
    @PostMapping("/like")
    public UserInteractionResultDTO likeReview(@RequestParam(name = "username") String username,
            @RequestBody UserReviewInteractionDTO interactionDTO) {
        UserInteractionResultDTO updatedLikes = reviewService.likeOrDislikeReview(username, interactionDTO);

        interactionDTO.setInteraction(ReviewInteraction.LIKE);

        ProducerInteractionDTO producerInteractionDTO = new ProducerInteractionDTO(username,
                interactionDTO.getReviewid(), interactionDTO.getAppid(), NotificationType.LIKE);

        reviewInteractionProducer.sendReviewInteraction(producerInteractionDTO);

        return updatedLikes;

    }

    // Done
    @PostMapping("/dislike")
    public UserInteractionResultDTO dislikeReview(@RequestParam(name = "username") String username,
            @RequestBody UserReviewInteractionDTO interactionDTO) {

        UserInteractionResultDTO updatedDislikes = reviewService.likeOrDislikeReview(username, interactionDTO);

        interactionDTO.setInteraction(ReviewInteraction.DISLIKE);

        ProducerInteractionDTO producerInteractionDTO = new ProducerInteractionDTO(username,
                interactionDTO.getReviewid(), interactionDTO.getAppid(), NotificationType.DISLIKE);

        reviewInteractionProducer.sendReviewInteraction(producerInteractionDTO);
        return updatedDislikes;
    }

    // Done
    @GetMapping("/games/{appid}")
    public List<ReviewWithLikedDTO> getAllReviewsByGame(@PathVariable Integer appid) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return reviewService.getAllReviewsByGame(appid, auth.getName());
    }

}
