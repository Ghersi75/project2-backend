package com.team2.backend.Service;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team2.backend.DTO.Review.NewReviewDTO;
import com.team2.backend.DTO.Review.UpdateReviewDTO;
import com.team2.backend.DTO.UserReviewInteraction.UserReviewInteractionDTO;
import com.team2.backend.Enums.ReviewInteraction;
import com.team2.backend.Enums.UserRole;
import com.team2.backend.Exceptions.*;
import com.team2.backend.Models.Game;
import com.team2.backend.Models.Review;
import com.team2.backend.Models.User;
import com.team2.backend.Models.UserReviewInteraction;
import com.team2.backend.Repository.GameRepository;
import com.team2.backend.Repository.ReviewRepository;
import com.team2.backend.Repository.UserRepository;
import com.team2.backend.Repository.UserReviewInteractionRepository;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private UserReviewInteractionRepository userReviewInteractionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    public void createReview(NewReviewDTO reviewDTO) {
        // Save review to the database
        // ...
       User user = userRepository.findById(reviewDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

       Game game =  gameRepository.findById(reviewDTO.getGameId())
                .orElseThrow(() -> new ResourceNotFoundException("Game not found"));
                Review review = new Review();
                
                review.setUser(user);
                review.setGame(game);
                review.setContent(reviewDTO.getContent());
        reviewRepository.save(review);
        // Send review to Kafka
        kafkaProducerService.sendMessage("reviews", "New review created: " + review.getContent());
    }

    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));


        reviewRepository.delete(review);
        kafkaProducerService.sendMessage("reviews", "Review deleted: " + review.getContent());
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
            UserReviewInteractionDTO userReviewInteractionDTO = new UserReviewInteractionDTO(user, review, interactionDTO.getInteraction());
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
