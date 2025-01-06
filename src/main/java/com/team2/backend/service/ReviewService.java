package com.team2.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team2.backend.dto.review.NewReviewDTO;
import com.team2.backend.dto.review.UpdateReviewDTO;
import com.team2.backend.dto.userreviewinteraction.UserReviewInteractionDTO;
import com.team2.backend.enums.ReviewInteraction;
import com.team2.backend.enums.UserRole;
import com.team2.backend.exceptions.*;
import com.team2.backend.models.Review;
import com.team2.backend.models.User;
import com.team2.backend.models.UserReviewInteraction;
import com.team2.backend.repository.ReviewRepository;
import com.team2.backend.repository.UserRepository;
import com.team2.backend.repository.UserReviewInteractionRepository;

import java.util.*;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;


    @Autowired
    private UserReviewInteractionRepository userReviewInteractionRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Review> getAllReviewsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return reviewRepository.findByUser(user);
    }

    public List<Review> getAllReviewsByGame(Integer appid) {
        return reviewRepository.findByAppid(appid);
    }

    public Review addReview(Long userId, NewReviewDTO reviewDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Review review = new Review(user, reviewDTO);

        return reviewRepository.save(review);
    }

    public void deleteReview(Long userId, Long reviewId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (user.getUserRole() == UserRole.CONTRIBUTOR && !review.getUser().equals(user)) {
            throw new ForbiddenException("You can only delete your own reviews");
        }
        reviewRepository.delete(review);
    }

    public Review updateReview(Long userId, Long reviewId, UpdateReviewDTO updateReviewDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        if (user.getUserRole() == UserRole.CONTRIBUTOR && !review.getUser().equals(user)) {
            throw new ForbiddenException("You can only edit your own reviews");
        }
        review.setContent(updateReviewDTO.getContent());

        return reviewRepository.save(review);
    }

    public void likeOrDislikeReview(Long userId, UserReviewInteractionDTO interactionDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Review review = reviewRepository.findById(interactionDTO.getReviewid())
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (review.getUser().equals(user)) {
            throw new ForbiddenException("You cannot like or dislike your own review");
        }

        UserReviewInteraction existingInteraction = userReviewInteractionRepository
                .findByUseridAndReviewid(userId, review.getId())
                .orElse(null);

        if (existingInteraction != null) {
            if (existingInteraction.getInteraction() == interactionDTO.getInteraction()) {
                userReviewInteractionRepository.delete(existingInteraction);
                if (existingInteraction.getInteraction() == ReviewInteraction.LIKE) {
                    review.setLikes(review.getLikes() - 1);
                } else if (existingInteraction.getInteraction() == ReviewInteraction.DISLIKE) {
                    review.setDislikes(review.getDislikes() - 1);
                }
                reviewRepository.save(review);
            } else {
                updateInteraction(existingInteraction, interactionDTO.getInteraction());
            }
        } else {
            UserReviewInteractionDTO userReviewInteractionDTO = new UserReviewInteractionDTO(userId, review.getId(),
                    interactionDTO.getInteraction());
            UserReviewInteraction newInteraction = new UserReviewInteraction(userReviewInteractionDTO);
            userReviewInteractionRepository.save(newInteraction);

            if (interactionDTO.getInteraction() == ReviewInteraction.LIKE) {
                review.setLikes(review.getLikes() + 1);
            } else if (interactionDTO.getInteraction() == ReviewInteraction.DISLIKE) {
                review.setDislikes(review.getDislikes() + 1);
            }
            reviewRepository.save(review);
        }
    }

    public void updateInteraction(UserReviewInteraction interaction, ReviewInteraction newInteraction) {
        Optional<Review> newreview = reviewRepository.findById(interaction.getReviewid());
        if(newreview.isPresent()){
            Review review = newreview.get();
            if (interaction.getInteraction() == ReviewInteraction.LIKE) {
                review.setLikes(review.getLikes() - 1);
            } else if (interaction.getInteraction() == ReviewInteraction.DISLIKE) {
                review.setDislikes(review.getDislikes() - 1);
            }
    
            if (newInteraction == ReviewInteraction.LIKE) {
                review.setLikes(review.getLikes() + 1);
            } else if (newInteraction == ReviewInteraction.DISLIKE) {
                review.setDislikes(review.getDislikes() + 1);
            }
    
            interaction.setInteraction(newInteraction);
            userReviewInteractionRepository.save(interaction);
            reviewRepository.save(review);
        }else{
            throw new ResourceNotFoundException("Invalid review");
        }
    }
}
