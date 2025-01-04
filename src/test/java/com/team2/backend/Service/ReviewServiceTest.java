package com.team2.backend.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.team2.backend.DTO.Review.NewReviewDTO;
import com.team2.backend.DTO.Review.UpdateReviewDTO;
import com.team2.backend.DTO.UserReviewInteraction.UserReviewInteractionDTO;
import com.team2.backend.Exceptions.ResourceNotFoundException;
import com.team2.backend.Exceptions.UserNotFoundException;
import com.team2.backend.Exceptions.ForbiddenException;
import com.team2.backend.Models.Game;
import com.team2.backend.Models.Review;
import com.team2.backend.Models.User;
import com.team2.backend.Models.UserReviewInteraction;
import com.team2.backend.Repository.ReviewRepository;
import com.team2.backend.Repository.UserRepository;
import com.team2.backend.Repository.GameRepository;
import com.team2.backend.Repository.UserReviewInteractionRepository;
import com.team2.backend.Enums.*;

import java.util.*;

public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserReviewInteractionRepository userReviewInteractionRepository;

    @InjectMocks
    private ReviewService reviewService;

    private User user;
    private Game game;
    private Review review;
    private NewReviewDTO newReviewDTO;
    private UpdateReviewDTO updateReviewDTO;
    private UserReviewInteractionDTO interactionDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUserRole(UserRole.CONTRIBUTOR);

        game = new Game();
        game.setId(1L);

        review = new Review();
        review.setId(1L);
        review.setUser(user);
        review.setGame(game);

        newReviewDTO = new NewReviewDTO();
        newReviewDTO.setGame(game);
        newReviewDTO.setContent("Great game!");

        updateReviewDTO = new UpdateReviewDTO();
        updateReviewDTO.setContent("Updated content");

        interactionDTO = new UserReviewInteractionDTO();
        interactionDTO.setInteraction(ReviewInteraction.LIKE);
        interactionDTO.setReview(new Review());
        interactionDTO.getReview().setId(1L);
    }

    @Test
    void addReview_ShouldAddReviewToGame() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        Review addedReview = reviewService.addReview(1L, newReviewDTO);

        assertNotNull(addedReview);
        assertEquals("Great game!", addedReview.getContent());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void addReview_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> reviewService.addReview(1L, newReviewDTO));
    }

    @Test
    void addReview_ShouldThrowException_WhenGameNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gameRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reviewService.addReview(1L, newReviewDTO));
    }

    @Test
    void deleteReview_ShouldRemoveReview() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        reviewService.deleteReview(1L, 1L);

        verify(reviewRepository, times(1)).delete(review);
    }

    @Test
    void deleteReview_ShouldThrowException_WhenReviewNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reviewService.deleteReview(1L, 1L));
    }

    @Test
    void deleteReview_ShouldThrowException_WhenUserIsNotOwner() {
        User otherUser = new User();
        otherUser.setId(2L);
        review.setUser(otherUser);  // The review is not owned by the current user

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        assertThrows(ForbiddenException.class, () -> reviewService.deleteReview(1L, 1L));
    }

    @Test
    void updateReview_ShouldUpdateReviewContent() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        Review updatedReview = reviewService.updateReview(1L, 1L, updateReviewDTO);

        assertEquals("Updated content", updatedReview.getContent());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void updateReview_ShouldThrowException_WhenReviewNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reviewService.updateReview(1L, 1L, updateReviewDTO));
    }

    @Test
    void updateReview_ShouldThrowException_WhenUserIsNotOwner() {
        User otherUser = new User();
        otherUser.setId(2L);
        review.setUser(otherUser);  // The review is not owned by the current user

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        assertThrows(ForbiddenException.class, () -> reviewService.updateReview(1L, 1L, updateReviewDTO));
    }

    @Test
    void likeOrDislikeReview_ShouldLikeReview() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(userReviewInteractionRepository.findByUserAndReview(user, review)).thenReturn(Optional.empty());

        reviewService.likeOrDislikeReview(1L, interactionDTO);

        assertEquals(1, review.getLikes());
        verify(userReviewInteractionRepository, times(1)).save(any(UserReviewInteraction.class));
    }

    @Test
    void likeOrDislikeReview_ShouldThrowException_WhenUserLikesOwnReview() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        interactionDTO.getReview().setUser(user);  // User tries to like their own review

        assertThrows(ForbiddenException.class, () -> reviewService.likeOrDislikeReview(1L, interactionDTO));
    }

    @Test
    void likeOrDislikeReview_ShouldThrowException_WhenReviewNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reviewService.likeOrDislikeReview(1L, interactionDTO));
    }
}