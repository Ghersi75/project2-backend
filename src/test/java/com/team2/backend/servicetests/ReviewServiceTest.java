/* package com.team2.backend.servicetests;

import com.team2.backend.service.*;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserReviewInteractionRepository userReviewInteractionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewService reviewService;

    private User user;
    private Review review;
    private NewReviewDTO newReviewDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUsername("testUser");
        user.setUserRole(UserRole.CONTRIBUTOR);

        review = new Review();
        review.setId(1L);
        review.setUser(user);
        review.setContent("This is a review.");

        newReviewDTO = new NewReviewDTO();
        newReviewDTO.setContent("New Review Content");
    }

    @Test
    void testGetAllReviewsByUser() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(reviewRepository.findByUser(user)).thenReturn(List.of(review));

        List<Review> reviews = reviewService.getAllReviewsByUser("testUser");

        assertNotNull(reviews);
        assertEquals(1, reviews.size());
        verify(userRepository).findByUsername("testUser");
        verify(reviewRepository).findByUser(user);
    }

    @Test
    void testAddReview() {
        NewReviewDTO newReviewDTO = new NewReviewDTO();
        newReviewDTO.setAppid(123); // Set a valid appid
        newReviewDTO.setContent("Great game!");
    
        User user = new User();
        user.setUsername("testUser");
    
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(reviewRepository.save(any(Review.class))).thenAnswer(i -> i.getArgument(0));
    
        ReviewDTO result = reviewService.addReview("testUser", newReviewDTO);
    
        assertNotNull(result);
        assertEquals("Great game!", result.getContent());
    
        verify(userRepository).findByUsername("testUser");
        verify(reviewRepository).save(any(Review.class));
    }
    

    @Test
    void testDeleteReview() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        reviewService.deleteReview("testUser", 1L);

        verify(userRepository).findByUsername("testUser");
        verify(reviewRepository).findById(1L);
        verify(reviewRepository).delete(review);
    }

    @Test
    void testUpdateReview() {
        UpdateReviewDTO updateReviewDTO = new UpdateReviewDTO();
        updateReviewDTO.setContent("Updated Content");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        reviewService.updateReview("testUser", 1L, updateReviewDTO);

        assertEquals("Updated Content", review.getContent());
        verify(userRepository).findByUsername("testUser");
        verify(reviewRepository).findById(1L);
        verify(reviewRepository).save(review);
    }

    @Test
    void testLikeOrDislikeReview_Like() {
        User differentUser = new User();
        differentUser.setUsername("differentUser");
    
        review.setUser(differentUser);
    
        UserReviewInteractionDTO interactionDTO = new UserReviewInteractionDTO(1L, 123, ReviewInteraction.LIKE);
    
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(userReviewInteractionRepository.findByUserAndReview(user, review)).thenReturn(Optional.empty());
    
        UserInteractionResultDTO resultDTO = reviewService.likeOrDislikeReview("testUser", interactionDTO);
    
        assertNotNull(resultDTO);
        assertEquals(1, review.getLikes());
    
        verify(userRepository).findByUsername("testUser");
        verify(reviewRepository, times(2)).findById(1L);
        verify(userReviewInteractionRepository).save(any(UserReviewInteraction.class));
    }
    

    @Test
    void testLikeOrDislikeReview_Dislike() {
        User differentUser = new User();
        differentUser.setUsername("differentUser");
    
        review.setUser(differentUser);
    
        UserReviewInteractionDTO interactionDTO = new UserReviewInteractionDTO(1L, 123, ReviewInteraction.DISLIKE);
    
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L))
            .thenReturn(Optional.of(review))
            .thenReturn(Optional.of(review)); 
    
        when(userReviewInteractionRepository.findByUserAndReview(user, review)).thenReturn(Optional.empty());
    
        UserInteractionResultDTO resultDTO = reviewService.likeOrDislikeReview("testUser", interactionDTO);
    
        assertNotNull(resultDTO);
        assertEquals(1, review.getDislikes());
        
        verify(userRepository).findByUsername("testUser");
        verify(reviewRepository, times(2)).findById(1L); 
        verify(userReviewInteractionRepository).save(any(UserReviewInteraction.class));
    }

    @Test
    void testGetById() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        Review foundReview = reviewService.getbyId(1L);

        assertNotNull(foundReview);
        assertEquals(1L, foundReview.getId());
        verify(reviewRepository).findById(1L);
    }
} */