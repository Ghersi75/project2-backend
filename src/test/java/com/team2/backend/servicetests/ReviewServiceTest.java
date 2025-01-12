package com.team2.backend.servicetests;

import com.team2.backend.dto.review.NewReviewDTO;
import com.team2.backend.dto.review.ReviewDTO;
import com.team2.backend.dto.review.ReviewWithLikedDTO;
import com.team2.backend.dto.review.UpdateReviewDTO;
import com.team2.backend.dto.user.UserSignUpDTO;
import com.team2.backend.dto.userreviewinteraction.UserReviewInteractionDTO;
import com.team2.backend.enums.ReviewInteraction;
import com.team2.backend.enums.UserRole;
import com.team2.backend.exceptions.ForbiddenException;
import com.team2.backend.exceptions.UserNotFoundException;
import com.team2.backend.exceptions.ResourceNotFoundException;
import com.team2.backend.models.Review;
import com.team2.backend.models.User;
import com.team2.backend.models.UserReviewInteraction;
import com.team2.backend.repository.ReviewRepository;
import com.team2.backend.repository.UserRepository;
import com.team2.backend.repository.UserReviewInteractionRepository;
import com.team2.backend.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

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
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        review = new Review();
        review.setId(1L);
        review.setUser(user);
    }

    @Test
    void testGetAllReviewsByUser_Success() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(reviewRepository.findByUser(user)).thenReturn(List.of(review));

        List<Review> reviews = reviewService.getAllReviewsByUser(user.getUsername());

        assertNotNull(reviews);
        assertEquals(1, reviews.size());
        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(reviewRepository, times(1)).findByUser(user);
    }

    @Test
    void testGetAllReviewsByUser_UserNotFound() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> reviewService.getAllReviewsByUser(user.getUsername()));
        verify(userRepository, times(1)).findByUsername(user.getUsername());
    }

    @Test
    void testAddReview_Success() {
        NewReviewDTO newReviewDTO = new NewReviewDTO();
        newReviewDTO.setContent("Great game!");
        newReviewDTO.setAppid(123);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        ReviewDTO reviewDTO = reviewService.addReview(user.getUsername(), newReviewDTO);

        assertNotNull(reviewDTO);
        assertEquals(review.getId(), reviewDTO.getReviewId());
        assertEquals(user.getUsername(), reviewDTO.getUsername());
        assertEquals(user.getDisplayName(), reviewDTO.getDisplayName());
        assertEquals(review.getContent(), reviewDTO.getContent());
        assertEquals(review.getLikes(), reviewDTO.getLikes());
        assertEquals(review.getDislikes(), reviewDTO.getDislikes());
        assertEquals(review.getPostedAt(), reviewDTO.getPostedAt());
        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testDeleteReview_Success() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));

        reviewService.deleteReview(user.getUsername(), review.getId());

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(reviewRepository, times(1)).findById(review.getId());
        verify(reviewRepository, times(1)).delete(review);
    }

    @Test
    void testDeleteReview_Forbidden() {
        User contributorUser = new User(2L, "contributorUser", "Contributor User", "password", UserRole.CONTRIBUTOR,
                null, null, null);
        User reviewOwner = new User(1L, "reviewOwner", "Review Owner", "password", UserRole.CONTRIBUTOR, null, null,
                null);

        Review review = new Review(reviewOwner, new NewReviewDTO("This is a test review.", "New Game", 123));

        when(userRepository.findByUsername(contributorUser.getUsername())).thenReturn(Optional.of(contributorUser));
        when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));

        assertThrows(ForbiddenException.class, () -> {
            reviewService.deleteReview(contributorUser.getUsername(), review.getId());
        });

        verify(reviewRepository, never()).delete(review);
    }

    @Test
    void testGetAllReviewsByGame_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("Test User");
        user.setDisplayName("testUser");
        user.setPassword("password");
        user.setUserRole(UserRole.CONTRIBUTOR);

        Review review = new Review();
        review.setId(1L);
        review.setAppid(123);
        review.setContent("This is review 1.");
        review.setLikes(0);
        review.setDislikes(0);
        review.setPostedAt(null);
        review.setUser(user);

        when(reviewRepository.findByAppidOrderByPostedAtDesc(123))
                .thenReturn(List.of(review));

        when(reviewRepository.findById(1L))
                .thenReturn(Optional.of(review));

        when(userRepository.findByUsername("Test User"))
                .thenReturn(Optional.of(user));

        UserReviewInteraction interaction = new UserReviewInteraction();
        interaction.setUser(user);
        interaction.setReview(review);
        interaction.setInteraction(ReviewInteraction.LIKE);

        when(userReviewInteractionRepository.findByUserAndReview(user, review))
                .thenReturn(Optional.of(interaction));

        List<ReviewWithLikedDTO> result = reviewService.getAllReviewsByGame(123, "Test User");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test User", result.get(0).getUsername());
        assertNotNull(result.get(0).getLikedByUser());
    }

    @Test
    void testUpdateInteraction_LikeToDislike() {
        User user = new User();
        user.setId(1L);
        user.setUsername("Test User");
        user.setDisplayName("Test Display Name");

        Review review = new Review();
        review.setId(1L);
        review.setAppid(123);
        review.setContent("Test review content.");
        review.setLikes(5);
        review.setDislikes(2);

        UserReviewInteraction existingInteraction = new UserReviewInteraction();
        existingInteraction.setUser(user);
        existingInteraction.setReview(review);
        existingInteraction.setInteraction(ReviewInteraction.LIKE);

        when(userReviewInteractionRepository.findByUserAndReview(user, review))
                .thenReturn(Optional.of(existingInteraction));
        when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));

        reviewService.updateInteraction(existingInteraction, ReviewInteraction.DISLIKE);

        verify(userReviewInteractionRepository).save(existingInteraction);
        verify(reviewRepository).save(review);

        assertEquals(4, review.getLikes());
        assertEquals(3, review.getDislikes());
        assertEquals(ReviewInteraction.DISLIKE, existingInteraction.getInteraction());
    }

    @Test
    void testUpdateInteraction_DislikeToLike() {
        User user = new User();
        user.setId(1L);
        user.setUsername("Test User");
        user.setDisplayName("Test Display Name");

        Review review = new Review();
        review.setId(1L);
        review.setAppid(123);
        review.setContent("Test review content.");
        review.setLikes(2);
        review.setDislikes(5);

        UserReviewInteraction existingInteraction = new UserReviewInteraction();
        existingInteraction.setUser(user);
        existingInteraction.setReview(review);
        existingInteraction.setInteraction(ReviewInteraction.DISLIKE);

        when(userReviewInteractionRepository.findByUserAndReview(user, review))
                .thenReturn(Optional.of(existingInteraction));
        when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));

        reviewService.updateInteraction(existingInteraction, ReviewInteraction.LIKE);

        verify(userReviewInteractionRepository).save(existingInteraction);
        verify(reviewRepository).save(review);

        assertEquals(3, review.getLikes());
        assertEquals(4, review.getDislikes());
        assertEquals(ReviewInteraction.LIKE, existingInteraction.getInteraction());
    }

    @Test
    void testUpdateInteraction_SameInteraction() {
        // Mock data setup
        User user = new User();
        user.setId(1L);
        user.setUsername("Test User");
        user.setDisplayName("Test Display Name");

        Review review = new Review();
        review.setId(1L);
        review.setAppid(123);
        review.setContent("Test review content.");
        review.setLikes(3);
        review.setDislikes(2);

        UserReviewInteraction existingInteraction = new UserReviewInteraction();
        existingInteraction.setUser(user);
        existingInteraction.setReview(review);
        existingInteraction.setInteraction(ReviewInteraction.LIKE);

        when(userReviewInteractionRepository.findByUserAndReview(user, review))
                .thenReturn(Optional.of(existingInteraction));
        when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));

        reviewService.updateInteraction(existingInteraction, ReviewInteraction.LIKE);

        verify(userReviewInteractionRepository).save(existingInteraction);

        assertEquals(3, review.getLikes());
        assertEquals(2, review.getDislikes());
        assertEquals(ReviewInteraction.LIKE, existingInteraction.getInteraction());
    }

    @Test
    void testUpdateReview_UserNotFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        UpdateReviewDTO updateReviewDTO = new UpdateReviewDTO("Updated content");

        assertThrows(UserNotFoundException.class, () -> reviewService.updateReview("testUser", 1L, updateReviewDTO));
    }

    @Test
    void testUpdateReview_ReviewNotFound() {
        UserSignUpDTO userSignUpDTO = new UserSignUpDTO("testUser", "Test User", "password", "CONTRIBUTOR");
        User user = new User(userSignUpDTO);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        UpdateReviewDTO updateReviewDTO = new UpdateReviewDTO("Updated content");

        assertThrows(ResourceNotFoundException.class,
                () -> reviewService.updateReview("testUser", 1L, updateReviewDTO));
    }

    @Test
    void testUpdateReview_UserNotOwner() {
        UserSignUpDTO userSignUpDTO1 = new UserSignUpDTO("testUser", "Test User", "password", "CONTRIBUTOR");
        UserSignUpDTO userSignUpDTO2 = new UserSignUpDTO("anotherUser", "Another User", "password", "CONTRIBUTOR");
        User user = new User(userSignUpDTO1);
        User anotherUser = new User(userSignUpDTO2);
        Review review = new Review(1L, anotherUser, 123, "Original content", 0, 0, null, null);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        UpdateReviewDTO updateReviewDTO = new UpdateReviewDTO("Updated content");

        assertThrows(ForbiddenException.class, () -> reviewService.updateReview("testUser", 1L, updateReviewDTO));
    }

    @Test
    @Transactional
    void testUpdateReview_Success() {
        UserSignUpDTO userSignUpDTO = new UserSignUpDTO("testUser", "Test User", "password", "CONTRIBUTOR");
        User user = new User(userSignUpDTO);
        Review review = new Review(1L, user, 123, "Original content", 0, 0, null, null);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        UpdateReviewDTO updateReviewDTO = new UpdateReviewDTO("Updated content");

        reviewService.updateReview("testUser", 1L, updateReviewDTO);

        assertEquals("Updated content", review.getContent());
        verify(reviewRepository, times(1)).save(review);
    }

    @Test
    void testLikeOrDislikeReview_UserNotFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        UserReviewInteractionDTO interactionDTO = new UserReviewInteractionDTO(1L, 123, "GameName", ReviewInteraction.LIKE);

        assertThrows(ResourceNotFoundException.class, () ->
                reviewService.likeOrDislikeReview("testUser", interactionDTO));
    }

    @Test
    void testLikeOrDislikeReview_ReviewNotFound() {
        User user = new User(new UserSignUpDTO("testUser", "Test User", "password", "CONTRIBUTOR"));
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        UserReviewInteractionDTO interactionDTO = new UserReviewInteractionDTO(1L, 123, "GameName", ReviewInteraction.LIKE);

        assertThrows(ResourceNotFoundException.class, () ->
                reviewService.likeOrDislikeReview("testUser", interactionDTO));
    }

    @Test
    void testLikeOrDislikeReview_UserLikingOwnReview() {
        User user = new User(new UserSignUpDTO("testUser", "Test User", "password", "CONTRIBUTOR"));
        Review review = new Review(1L, user, 123, "Content", 0, 0, null, null);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        UserReviewInteractionDTO interactionDTO = new UserReviewInteractionDTO(1L, 123, "GameName", ReviewInteraction.LIKE);

        assertThrows(ForbiddenException.class, () ->
                reviewService.likeOrDislikeReview("testUser", interactionDTO));
    }

    @Test
    void testLikeOrDislikeReview_ToggleInteraction() {
        User user = new User(new UserSignUpDTO("testUser", "Test User", "password", "CONTRIBUTOR"));
        Review review = new Review(1L, new User(), 123, "Content", 1, 0, null, null);
        UserReviewInteraction existingInteraction = new UserReviewInteraction(new UserReviewInteractionDTO(1L, 123, "GameName", ReviewInteraction.LIKE), user, review);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(userReviewInteractionRepository.findByUserAndReview(user, review)).thenReturn(Optional.of(existingInteraction));

        UserReviewInteractionDTO interactionDTO = new UserReviewInteractionDTO(1L, 123, "GameName", ReviewInteraction.LIKE);

        reviewService.likeOrDislikeReview("testUser", interactionDTO);

        verify(userReviewInteractionRepository).delete(existingInteraction);
        assertEquals(0, review.getLikes());
        verify(reviewRepository, times(1)).save(review);
    }
    @Test
    void testLikeOrDislikeReview_NewInteraction() {
        User user = new User(new UserSignUpDTO("testUser", "Test User", "password", "CONTRIBUTOR"));
        Review review = new Review(1L, new User(), 123, "Content", 0, 0, null, null);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(userReviewInteractionRepository.findByUserAndReview(user, review)).thenReturn(Optional.empty());

        UserReviewInteractionDTO interactionDTO = new UserReviewInteractionDTO(1L, 123, "GameName", ReviewInteraction.LIKE);

        reviewService.likeOrDislikeReview("testUser", interactionDTO);

        assertEquals(1, review.getLikes());
        verify(userReviewInteractionRepository, times(1)).save(any(UserReviewInteraction.class));
        verify(reviewRepository, times(1)).save(review);
    }
}
