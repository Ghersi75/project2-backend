package com.team2.backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team2.backend.Repository.ReviewRepository;
import com.team2.backend.Repository.UserRepository;
import com.team2.backend.Repository.UserReviewInteractionRepository;
import com.team2.backend.Repository.GameRepository;
import com.team2.backend.Models.Review;
import com.team2.backend.Models.UserReviewInteraction;
import com.team2.backend.Models.User;
import com.team2.backend.Models.Game;
import com.team2.backend.Enums.UserRole;
import com.team2.backend.DTO.Review.NewReviewDTO;
import com.team2.backend.DTO.UserReviewInteraction.UserReviewInteractionDTO;
import com.team2.backend.Enums.ReviewInteraction;
import com.team2.backend.Exceptions.ResourceNotFoundException;
import com.team2.backend.Exceptions.ForbiddenException;
import com.team2.backend.Exceptions.UnauthorizedException;
import com.team2.backend.Exceptions.UserNotFoundException;

import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private UserReviewInteractionRepository userReviewInteractionRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    public Review addReview(Long userId, NewReviewDTO reviewDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Game game = gameRepository.findById(reviewDTO.getGameId())
                .orElseThrow(() -> new ResourceNotFoundException("Game not found"));

        Review review = new Review();
        review.setUser(user);
        review.setGame(game);
        review.setContent(reviewDTO.getContent());

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



    public void likeOrDislikeReview(Long userId, UserReviewInteractionDTO interactionDTO) {
        User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    
        Review review = reviewRepository.findById(interactionDTO.getReview().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
    
        if (review.getUser().equals(user)) {
            throw new ForbiddenException("You cannot like or dislike your own review");
        }

        UserReviewInteraction existingInteraction = userReviewInteractionRepository
                .findByUserAndReview(user, review)
                .orElse(null);
    
        if (existingInteraction != null) {
            if (existingInteraction.getInteraction() != interactionDTO.getInteraction()) {
                updateInteraction(existingInteraction, interactionDTO.getInteraction());
            } else {
                throw new ForbiddenException("You have already performed this action");
            }
        } else {
            UserReviewInteraction newInteraction = new UserReviewInteraction(new UserReviewInteractionDTO(user, review, interactionDTO.getInteraction()));
    
            userReviewInteractionRepository.save(newInteraction);
  
            if (interactionDTO.getInteraction() == ReviewInteraction.LIKE) {
                review.setLikes(review.getLikes() + 1);
            } else if (interactionDTO.getInteraction() == ReviewInteraction.DISLIKE) {
                review.setDislikes(review.getDislikes() + 1);
            }
            reviewRepository.save(review);
        }
    }
    
    private void updateInteraction(UserReviewInteraction interaction, ReviewInteraction newInteraction) {
        Review review = interaction.getReview();
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
    }

}