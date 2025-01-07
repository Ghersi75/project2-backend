package com.team2.backend.servicetests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.team2.backend.dto.review.NewReviewDTO;
import com.team2.backend.dto.review.ReviewDTO;
import com.team2.backend.dto.userreviewinteraction.UserReviewInteractionDTO;
import com.team2.backend.enums.*;
import com.team2.backend.exceptions.ResourceNotFoundException;
import com.team2.backend.exceptions.UserNotFoundException;
import com.team2.backend.models.Review;
import com.team2.backend.models.User;
import com.team2.backend.repository.ReviewRepository;
import com.team2.backend.repository.UserRepository;
import com.team2.backend.repository.UserReviewInteractionRepository;
import com.team2.backend.service.ReviewService;

import java.util.List;
import java.util.Optional;
import java.util.Arrays;

public class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserReviewInteractionRepository userReviewInteractionRepository;

    private User user;
    private Review review;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUsername("testUser");

        review = new Review();
        review.setId(1L);
        review.setUser(user);
    }

    @Test
    public void testGetAllReviewsByUser_UserExists() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(reviewRepository.findByUser(user)).thenReturn(List.of(review));

        List<Review> reviews = reviewService.getAllReviewsByUser("testUser");

        assertNotNull(reviews);
        assertEquals(1, reviews.size());
        assertEquals(review, reviews.get(0));
    }

    @Test
    public void testGetAllReviewsByUser_UserNotFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            reviewService.getAllReviewsByUser("testUser");
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testGetAllReviewsByGame() {
        Integer appid = 123;
        Review review1 = new Review();
        Review review2 = new Review();
        List<Review> reviews = Arrays.asList(review1, review2);

        
        when(reviewRepository.findByAppid(appid)).thenReturn(reviews);

        List<ReviewDTO> result = reviewService.getAllReviewsByGame(appid);

        assertNotNull(result);
        assertEquals(2, result.size()); 
        verify(reviewRepository, times(1)).findByAppid(appid);
    }

    @Test
    public void testAddReview_UserExists() {
        User user = new User();
        user.setUsername("testUser");

        NewReviewDTO newReviewDTO = new NewReviewDTO();
        newReviewDTO.setAppid(123);
        newReviewDTO.setContent("This is a test review");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        Review review = new Review(user, newReviewDTO);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        ReviewDTO reviewDTO = reviewService.addReview("testUser", newReviewDTO);
        assertNotNull(reviewDTO);
        assertEquals(newReviewDTO.getContent(), reviewDTO.getContent());
    }

    @Test
    public void testAddReview_UserNotFound() {
        NewReviewDTO newReviewDTO = new NewReviewDTO();
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            reviewService.addReview("testUser", newReviewDTO);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testDeleteReview_ReviewExists() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        assertDoesNotThrow(() -> reviewService.deleteReview("testUser", 1L));
        verify(reviewRepository, times(1)).delete(review);
    }

    @Test
    public void testDeleteReview_ReviewNotFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            reviewService.deleteReview("testUser", 1L);
        });

        assertEquals("Review not found", exception.getMessage());
    }

    @Test
    public void testLikeOrDislikeReview_UserExists() {
        User anotherUser = new User();
        anotherUser.setUsername("anotherUser");

        UserReviewInteractionDTO interactionDTO = new UserReviewInteractionDTO(1L, ReviewInteraction.LIKE);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(userReviewInteractionRepository.findByUserAndReviewid(user, 1L)).thenReturn(Optional.empty());

        review.setUser(anotherUser);

        assertDoesNotThrow(() -> reviewService.likeOrDislikeReview("testUser", interactionDTO));
    }

    @Test
    public void testLikeOrDislikeReview_ReviewNotFound() {
        UserReviewInteractionDTO interactionDTO = new UserReviewInteractionDTO(1L, ReviewInteraction.LIKE);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            reviewService.likeOrDislikeReview("testUser", interactionDTO);
        });

        assertEquals("Review not found", exception.getMessage());
    }

}