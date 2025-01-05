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
import com.team2.backend.Models.Review;
import com.team2.backend.Models.User;
import com.team2.backend.Models.UserReviewInteraction;
import com.team2.backend.Repository.ReviewRepository;
import com.team2.backend.Repository.UserRepository;
import com.team2.backend.Repository.UserReviewInteractionRepository;
import com.team2.backend.Enums.*;

import java.util.*;

public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserReviewInteractionRepository userReviewInteractionRepository;

    @InjectMocks
    private ReviewService reviewService;

    private User user;
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

        review = new Review();
        review.setId(1L);
        review.setUser(user);
        review.setAppId(12345);

        newReviewDTO = new NewReviewDTO();
        newReviewDTO.setAppid(12345);
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
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        NewReviewDTO newReviewDTO = new NewReviewDTO("Great game!",12345);
    
        Review savedReview = new Review(user, newReviewDTO);
        savedReview.setId(1L);
    
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);
    
        Review result = reviewService.addReview(1L, newReviewDTO);

        assertNotNull(result, "Review should not be null");
        assertEquals("Great game!", result.getContent(), "Review content should match");
        assertEquals(user, result.getUser(), "Review should be associated with the correct user");
        assertEquals(12345, result.getAppId(), "Review should be linked to the correct game");
        verify(userRepository, times(1)).findById(1L);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void addReview_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> reviewService.addReview(1L, newReviewDTO));
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
        User user = new User();
        user.setId(1L);
    
        Review review = new Review();
        review.setId(1L);
        review.setContent("Old content");
        review.setUser(user);
    
        UpdateReviewDTO updateReviewDTO = new UpdateReviewDTO();
        updateReviewDTO.setContent("Updated content");
    
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));
    
        Review updatedReview = reviewService.updateReview(1L, 1L, updateReviewDTO);
    
        assertNotNull(updatedReview, "Updated review should not be null");
        assertEquals("Updated content", updatedReview.getContent(), "Content should be updated correctly");
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
        User reviewOwner = new User();
        reviewOwner.setId(1L);
    
        User interactingUser = new User();
        interactingUser.setId(2L);
    
        Review review = new Review();
        review.setId(1L);
        review.setUser(reviewOwner);
        review.setLikes(0);
    
        UserReviewInteractionDTO interactionDTO = new UserReviewInteractionDTO();
        interactionDTO.setReview(review);
        interactionDTO.setInteraction(ReviewInteraction.LIKE);
    
        when(userRepository.findById(2L)).thenReturn(Optional.of(interactingUser));
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(userReviewInteractionRepository.findByUserAndReview(interactingUser, review))
            .thenReturn(Optional.empty());
    
        reviewService.likeOrDislikeReview(2L, interactionDTO);
    
        assertEquals(1, review.getLikes(), "The review's like count should be incremented");
        verify(userReviewInteractionRepository, times(1)).save(any(UserReviewInteraction.class));
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void likeOrDislikeReview_ShouldThrowException_WhenUserLikesOwnReview() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        interactionDTO.getReview().setUser(user); 

        assertThrows(ForbiddenException.class, () -> reviewService.likeOrDislikeReview(1L, interactionDTO));
    }

    @Test
    void likeOrDislikeReview_ShouldThrowException_WhenReviewNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reviewService.likeOrDislikeReview(1L, interactionDTO));
    }
}