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

import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    public Review addReview(NewReviewDTO reviewDTO) {
        User user = getLoggedInUser(); //this will be changed, it's just for testing
        if (user == null) {
            throw new UnauthorizedException("You must be logged in to add a review");
        }

        Game game = gameRepository.findById(reviewDTO.getGameId())
                .orElseThrow(() -> new ResourceNotFoundException("Game not found"));

        Review review = new Review();
        review.setUser(user);
        review.setGame(game);
        review.setContent(reviewDTO.getContent());

        return reviewRepository.save(review);
    }

    public void deleteReview(Long id) {
        User user = getLoggedInUser();
        if (user == null) {
            throw new UnauthorizedException("You must be logged in to delete a review");
        }
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (user.getUserRole() == UserRole.CONTRIBUTOR && !review.getUser().equals(user)) {
            throw new ForbiddenException("You can only delete your own reviews");
        }
        reviewRepository.delete(review);
    }


    //Temporary
    private User getLoggedInUser() {
        return userRepository.findByUsername("john_doe")
                .orElse(null);
    }
}