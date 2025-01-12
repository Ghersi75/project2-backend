/* package com.team2.backend.servicetests;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.team2.backend.dto.review.NewReviewDTO;
import com.team2.backend.dto.review.ReviewDTO;
import com.team2.backend.dto.review.ReviewWithLikedDTO;
import com.team2.backend.dto.review.UpdateReviewDTO;
import com.team2.backend.dto.userreviewinteraction.ProducerInteractionDTO;
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
import com.team2.backend.service.ReviewService;

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
    private User user2;
    private Review review;
    private ProducerInteractionDTO interactionDTO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUsername("testUser");
        user.setUserRole(UserRole.CONTRIBUTOR);

        user2 = new User();
        user2.setUsername("testUser2");

        review = new Review();
        review.setId(1L);
        review.setUser(user);

        interactionDTO = new ProducerInteractionDTO("testuser", 1L, ReviewInteraction.LIKE);
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
    public void testGetAllReviewsByGame_UserExistsWithoutInteractions() {
        Integer appid = 123;
        String username = "testUser";

        User user = new User();
        user.setUsername(username);

        Review review = new Review();
        review.setId(1L);
        review.setAppid(appid);
        review.setUser(user2);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(reviewRepository.findByAppidOrderByPostedAtDesc(appid)).thenReturn(List.of(review));
        when(userReviewInteractionRepository.findByUserAndReview(user, review)).thenReturn(Optional.empty());

        List<ReviewWithLikedDTO> result = reviewService.getAllReviewsByGame(appid, username);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getLikedByUser());
    }

    @Test
    public void testGetAllReviewsByGame_GameHasNoReviews() {
        Integer appid = 123;
        String username = "testUser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));
        when(reviewRepository.findByAppidOrderByPostedAtDesc(appid)).thenReturn(Collections.emptyList());

        List<ReviewWithLikedDTO> result = reviewService.getAllReviewsByGame(appid, username);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetAllReviewsByGame_UserExistsGameHasReviewsNoInteraction() {
        Integer appid = 123;
        String username = "testUser";

        User user = new User();
        user.setUsername(username);

        Review review = new Review();
        review.setId(1L);
        review.setAppid(appid);
        review.setUser(user);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(reviewRepository.findByAppidOrderByPostedAtDesc(appid)).thenReturn(List.of(review));
        when(userReviewInteractionRepository.findByUserAndReview(user, review)).thenReturn(Optional.empty());

        List<ReviewWithLikedDTO> result = reviewService.getAllReviewsByGame(appid, username);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getLikedByUser());
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
    void testUpdateReview_Success() {
        UpdateReviewDTO updateReviewDTO = new UpdateReviewDTO();
        updateReviewDTO.setContent("Updated content");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        reviewService.updateReview("testuser", 1L, updateReviewDTO);

        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testUpdateReview_UserNotFound() {
        UpdateReviewDTO updateReviewDTO = new UpdateReviewDTO();
        updateReviewDTO.setContent("Updated content");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> reviewService.updateReview("testuser", 1L, updateReviewDTO));
    }

    @Test
    void testUpdateReview_ReviewNotFound() {
        UpdateReviewDTO updateReviewDTO = new UpdateReviewDTO();
        updateReviewDTO.setContent("Updated content");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reviewService.updateReview("testuser", 1L, updateReviewDTO));
    }

@Test
void testUpdateReview_ForbiddenException() {
    User differentUser = new User();
    differentUser.setUsername("differentUser");
    differentUser.setUserRole(UserRole.CONTRIBUTOR);

    Review review = new Review();
    review.setId(1L);
    review.setUser(differentUser); // Review is owned by a different user

    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
    when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

    UpdateReviewDTO updateReviewDTO = new UpdateReviewDTO();
    updateReviewDTO.setContent("Updated content");

    assertThrows(ForbiddenException.class, () -> {
        reviewService.updateReview("testuser", 1L, updateReviewDTO);
    });
}

    @Test
    void testLikeOrDislikeReview_Success() {
        User reviewOwner = new User();
        reviewOwner.setUsername("reviewOwner");
        review.setUser(reviewOwner);
    
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(userReviewInteractionRepository.findByUserAndReview(user, review)).thenReturn(Optional.empty());
    
        reviewService.likeOrDislikeReview("testuser", interactionDTO);
    
        verify(userReviewInteractionRepository, times(1)).save(any(UserReviewInteraction.class));
        verify(reviewRepository, times(1)).save(review);
    }

    @Test
    void testLikeOrDislikeReview_UserNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reviewService.likeOrDislikeReview("testuser", interactionDTO));
    }

    @Test
    void testLikeOrDislikeReview_ReviewNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reviewService.likeOrDislikeReview("testuser", interactionDTO));
    }

    @Test
    void testLikeOrDislikeReview_ForbidenException() {
        review.setUser(user); // Same user

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        assertThrows(ForbiddenException.class, () -> reviewService.likeOrDislikeReview("testuser", interactionDTO));
    }

    @Test
    void testLikeOrDislikeReview_ExistingInteraction() {
        User reviewOwner = new User();
        reviewOwner.setUsername("reviewOwner");
        review.setUser(reviewOwner);
    
        UserReviewInteraction existingInteraction = new UserReviewInteraction();
        existingInteraction.setInteraction(ReviewInteraction.LIKE);
        existingInteraction.setUser(user);
    
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(userReviewInteractionRepository.findByUserAndReview(user, review)).thenReturn(Optional.of(existingInteraction));
    
        reviewService.likeOrDislikeReview("testuser", interactionDTO);
    
        verify(userReviewInteractionRepository, times(1)).delete(existingInteraction);
        verify(reviewRepository, times(1)).save(review);
    }

    @Test
    void testUpdateInteraction_LikeToDislike() {
        Review review = new Review();
        review.setId(1L);
        review.setLikes(5);
        review.setDislikes(3);

        UserReviewInteraction interaction = new UserReviewInteraction();
        interaction.setReview(review);
        interaction.setInteraction(ReviewInteraction.LIKE);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        reviewService.updateInteraction(interaction, ReviewInteraction.DISLIKE);

        assertEquals(4, review.getLikes());
        assertEquals(4, review.getDislikes());
        verify(reviewRepository).save(review);
        verify(userReviewInteractionRepository).save(interaction);
    }

    @Test
    void testUpdateInteraction_DislikeToLike() {
        Review review = new Review();
        review.setId(1L);
        review.setLikes(5);
        review.setDislikes(3);

        UserReviewInteraction interaction = new UserReviewInteraction();
        interaction.setReview(review);
        interaction.setInteraction(ReviewInteraction.DISLIKE);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        reviewService.updateInteraction(interaction, ReviewInteraction.LIKE);

        assertEquals(6, review.getLikes());
        assertEquals(2, review.getDislikes());
        verify(reviewRepository).save(review);
        verify(userReviewInteractionRepository).save(interaction);
    }

    @Test
    void testUpdateInteraction_InvalidReview() {
        UserReviewInteraction interaction = new UserReviewInteraction();
        Review review = new Review();
        review.setId(1L);
        interaction.setReview(review);

        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            reviewService.updateInteraction(interaction, ReviewInteraction.LIKE);
        });
    }
} */