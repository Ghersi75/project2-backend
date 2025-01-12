package com.team2.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.backend.dto.review.NewReviewDTO;
import com.team2.backend.dto.review.ReviewDTO;
import com.team2.backend.dto.review.ReviewWithLikedDTO;
import com.team2.backend.dto.review.UpdateReviewDTO;
import com.team2.backend.dto.userreviewinteraction.UserInteractionResultDTO;
import com.team2.backend.dto.userreviewinteraction.UserReviewInteractionDTO;
import com.team2.backend.enums.ReviewInteraction;
import com.team2.backend.enums.UserRole;
import com.team2.backend.exceptions.ForbiddenException;
import com.team2.backend.exceptions.ResourceNotFoundException;
import com.team2.backend.exceptions.UserNotFoundException;
import com.team2.backend.models.Review;
import com.team2.backend.models.User;
import com.team2.backend.models.UserReviewInteraction;
import com.team2.backend.repository.ReviewRepository;
import com.team2.backend.repository.UserRepository;
import com.team2.backend.repository.UserReviewInteractionRepository;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserReviewInteractionRepository userReviewInteractionRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Review> getAllReviewsByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return reviewRepository.findByUser(user);
    }

    // Done
    public List<ReviewWithLikedDTO> getAllReviewsByGame(Integer appid, String username) {
        return reviewRepository
                .findByAppidOrderByPostedAtDesc(appid)
                .stream()
                .map(old -> {
                    User foundUser = this.userRepository.findByUsername(username).orElse(null);
                    Review foundReview = this.reviewRepository.findById(old.getId()).orElse(null);
                    UserReviewInteraction foundInteraction = this.userReviewInteractionRepository
                            .findByUserAndReview(foundUser, foundReview).orElse(null);
                    if (foundInteraction == null) {
                        return new ReviewWithLikedDTO(old, null);
                    }
                    return new ReviewWithLikedDTO(old, foundInteraction.getInteraction());
                })
                .toList();
    }

    // Done
    @Transactional
    public ReviewDTO addReview(String username, NewReviewDTO reviewDTO) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Review review = new Review(user, reviewDTO);

        return new ReviewDTO(reviewRepository.save(review));
    }

    @Transactional
    public void deleteReview(String username, Long reviewId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (user.getUserRole() == UserRole.CONTRIBUTOR && !review.getUser().equals(user)) {
            throw new ForbiddenException("You can only delete your own reviews");
        }
        reviewRepository.delete(review);
    }

    @Transactional
    public void updateReview(String username, Long reviewId, UpdateReviewDTO updateReviewDTO) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        if (user.getUserRole() == UserRole.CONTRIBUTOR && !review.getUser().equals(user)) {
            throw new ForbiddenException("You can only edit your own reviews");
        }
        review.setContent(updateReviewDTO.getContent());

        reviewRepository.save(review);
    }

    // Done
    public UserInteractionResultDTO likeOrDislikeReview(String username, UserReviewInteractionDTO interactionDTO) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Review review = reviewRepository.findById(interactionDTO.getReviewid())
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (review.getUser().equals(user)) {
            throw new ForbiddenException("You cannot like or dislike your own review");
        }

        UserReviewInteraction existingInteraction = userReviewInteractionRepository
                .findByUserAndReview(user, review)
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
            UserReviewInteractionDTO userReviewInteractionDTO = new UserReviewInteractionDTO(review.getId(),review.getAppid(),interactionDTO.getGameName(),
                    interactionDTO.getInteraction());
            UserReviewInteraction newInteraction = new UserReviewInteraction(userReviewInteractionDTO, user, review);
            userReviewInteractionRepository.save(newInteraction);

            if (interactionDTO.getInteraction() == ReviewInteraction.LIKE) {
                review.setLikes(review.getLikes() + 1);
            } else if (interactionDTO.getInteraction() == ReviewInteraction.DISLIKE) {
                review.setDislikes(review.getDislikes() + 1);
            }
            reviewRepository.save(review);
        }
        review = reviewRepository.findById(interactionDTO.getReviewid())
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        return new UserInteractionResultDTO(review);
    }

    public void updateInteraction(UserReviewInteraction interaction, ReviewInteraction newInteraction) {
        Optional<Review> newreview = reviewRepository.findById(interaction.getReview().getId());
        if (newreview.isPresent()) {
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
        } else {
            throw new ResourceNotFoundException("Invalid review");
        }
    }
    
    public Review getbyId(Long id) {
        return reviewRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Review not found"));
    }
}
