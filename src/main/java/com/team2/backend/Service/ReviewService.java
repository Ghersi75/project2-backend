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
import com.team2.backend.DTO.UserReviewInteraction.UserReviewInteractionDTO;
import com.team2.backend.Enums.ReviewInteraction;
import com.team2.backend.Exceptions.ResourceNotFoundException;
import com.team2.backend.Exceptions.ForbiddenException;
import com.team2.backend.Exceptions.UnauthorizedException;

import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserReviewInteractionRepository userReviewInteractionRepository;

    public Review addReview(Long gameId, String content, int rating) {
        User user = getLoggedInUser();
        if (user == null) throw new UnauthorizedException("You must be logged in to leave a review");

        Game game = gameRepository.findById(gameId)
                                  .orElseThrow(() -> new ResourceNotFoundException("Game not found"));

        Review review = new Review();
        review.setUser(user);
        review.setGame(game);
        review.setContent(content);

        return reviewRepository.save(review);
    }

    public void likeReview(Long reviewId) {
        User user = getLoggedInUser(); 
        if (user == null) throw new UnauthorizedException("You must be logged in to like a review");

        Review review = reviewRepository.findById(reviewId)
                                        .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (review.getUser().equals(user)) {
            throw new ForbiddenException("You cannot like your own review");
        }
        Optional<UserReviewInteraction> interactionOpt = userReviewInteractionRepository
                .findByUserAndReview(user, review);

        if (interactionOpt.isPresent() && interactionOpt.get().getInteraction() == ReviewInteraction.LIKED) {
            throw new ForbiddenException("You have already liked this review");
        }

        review.setLikes(review.getLikes() + 1);
        reviewRepository.save(review);

        UserReviewInteractionDTO interactionDTO = new UserReviewInteractionDTO(user, review, ReviewInteraction.LIKED);
        userReviewInteractionRepository.save(interactionDTO);
    }

    public void dislikeReview(Long reviewId) {
        User user = getLoggedInUser();
        if (user == null) throw new UnauthorizedException("You must be logged in to dislike a review");

        Review review = reviewRepository.findById(reviewId)
                                        .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (review.getUser().equals(user)) {
            throw new ForbiddenException("You cannot dislike your own review");
        }

        Optional<UserReviewInteraction> interactionOpt = userReviewInteractionRepository
                .findByUserAndReview(user, review);

        if (interactionOpt.isPresent() && interactionOpt.get().getInteraction() == ReviewInteraction.DISLIKED) {
            throw new ForbiddenException("You have already disliked this review");
        }

        review.setDislikes(review.getDislikes() + 1);
        reviewRepository.save(review);

        UserReviewInteraction interaction = new UserReviewInteraction(user, review, ReviewInteraction.DISLIKED);
        userReviewInteractionRepository.save(interaction);
    }

    public void deleteReview(Long reviewId) {
        User user = getLoggedInUser(); 
        if (user == null) throw new UnauthorizedException("You must be logged in to delete a review");

        Review review = reviewRepository.findById(reviewId)
                                        .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (review.getUser().equals(user)) {
            reviewRepository.delete(review);
        } else if (user.getUserRole() == UserRole.MODERATOR) {
            reviewRepository.delete(review);
        } else {
            throw new ForbiddenException("You can only delete your own review or if you're a moderator");
        }
    }
}