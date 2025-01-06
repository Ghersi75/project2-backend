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
import com.team2.backend.dto.review.UpdateReviewDTO;
import com.team2.backend.dto.userreviewinteraction.UserReviewInteractionDTO;
import com.team2.backend.enums.*;
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
    private User user2;
    private Review review;
    private NewReviewDTO newReviewDTO;
    private UpdateReviewDTO updateReviewDTO;
    private UserReviewInteractionDTO interactionDTO;
    private UserReviewInteractionDTO interactionDTO2;
    private UserReviewInteraction interaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUserRole(UserRole.CONTRIBUTOR);

        user2 = new User();
        user2.setId(2L);
        user2.setUserRole(UserRole.CONTRIBUTOR);

        review = new Review();
        review.setId(1L);
        review.setUser(user);
        review.setAppid(12345);

        newReviewDTO = new NewReviewDTO();
        newReviewDTO.setAppid(12345);
        newReviewDTO.setContent("Great game!");

        updateReviewDTO = new UpdateReviewDTO();
        updateReviewDTO.setContent("Updated content");

        interactionDTO = new UserReviewInteractionDTO();
        interactionDTO.setInteraction(ReviewInteraction.LIKE);
        interactionDTO.setReview(new Review());
        interactionDTO.getReview().setId(1L);

        interactionDTO2 = new UserReviewInteractionDTO();
        interactionDTO2.setInteraction(ReviewInteraction.LIKE);
        interactionDTO2.setReview(review);
        interactionDTO2.setUserid(2L);

        interaction = new UserReviewInteraction(interactionDTO2);
    }

    @Test
    void getAllReviewsByUser_ShouldReturnListOfReviews_WhenUserExists() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");

        Review review1 = new Review();
        review1.setContent("Review 1");
        Review review2 = new Review();
        review2.setContent("Review 2");

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(reviewRepository.findByUser(mockUser)).thenReturn(Arrays.asList(review1, review2));

        List<Review> reviews = reviewService.getAllReviewsByUser(1L);

        assertNotNull(reviews);
        assertEquals(2, reviews.size());
        assertEquals("Review 1", reviews.get(0).getContent());
        assertEquals("Review 2", reviews.get(1).getContent());

        verify(userRepository, times(1)).findById(1L);
        verify(reviewRepository, times(1)).findByUser(mockUser);
    }

    @Test
    void getAllReviewsByUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> reviewService.getAllReviewsByUser(1L));

        verify(userRepository, times(1)).findById(1L);
        verifyNoInteractions(reviewRepository);
    }

    @Test
    void getAllReviewsByGame_ShouldReturnListOfReviews_WhenGameHasReviews() {
        Integer appid = 12345;

        Review review1 = new Review();
        review1.setContent("Great game!");
        Review review2 = new Review();
        review2.setContent("Not bad.");

        when(reviewRepository.findByAppid(appid)).thenReturn(Arrays.asList(review1, review2));

        List<Review> reviews = reviewService.getAllReviewsByGame(appid);

        assertNotNull(reviews);
        assertEquals(2, reviews.size());
        assertEquals("Great game!", reviews.get(0).getContent());
        assertEquals("Not bad.", reviews.get(1).getContent());

        verify(reviewRepository, times(1)).findByAppid(appid);
    }

    @Test
    void getAllReviewsByGame_ShouldReturnEmptyList_WhenNoReviewsForGame() {
        Integer appid = 12345;

        when(reviewRepository.findByAppid(appid)).thenReturn(Arrays.asList());

        List<Review> reviews = reviewService.getAllReviewsByGame(appid);

        assertNotNull(reviews);
        assertTrue(reviews.isEmpty());

        verify(reviewRepository, times(1)).findByAppid(appid);
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
        assertEquals(12345, result.getAppid(), "Review should be linked to the correct game");
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
        review.setUser(otherUser); 

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
        review.setUser(otherUser); 

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
        when(userReviewInteractionRepository.findByUseridAndReview(interactingUser.getId(), review))
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

    @Test
    void updateInteraction_ShouldUpdateLikeToDislike() {
        Review review = new Review();
        review.setLikes(1);
        review.setDislikes(0);

        UserReviewInteraction interaction = new UserReviewInteraction();
        interaction.setReview(review);
        interaction.setInteraction(ReviewInteraction.LIKE);

        reviewService.updateInteraction(interaction, ReviewInteraction.DISLIKE);

        assertEquals(0, review.getLikes(), "Likes should decrease by 1");
        assertEquals(1, review.getDislikes(), "Dislikes should increase by 1");
        assertEquals(ReviewInteraction.DISLIKE, interaction.getInteraction(), "Interaction should be updated to DISLIKE");

        verify(userReviewInteractionRepository, times(1)).save(interaction);
        verify(reviewRepository, times(1)).save(review);
    }

    @Test
    void updateInteraction_ShouldUpdateDislikeToLike() {
        Review review = new Review();
        review.setLikes(0);
        review.setDislikes(1);

        UserReviewInteraction interaction = new UserReviewInteraction();
        interaction.setReview(review);
        interaction.setInteraction(ReviewInteraction.DISLIKE);

        reviewService.updateInteraction(interaction, ReviewInteraction.LIKE);

        assertEquals(1, review.getLikes(), "Likes should increase by 1");
        assertEquals(0, review.getDislikes(), "Dislikes should decrease by 1");
        assertEquals(ReviewInteraction.LIKE, interaction.getInteraction(), "Interaction should be updated to LIKE");

        verify(userReviewInteractionRepository, times(1)).save(interaction);
        verify(reviewRepository, times(1)).save(review);
    }

    @Test
    void likeOrDislikeReview_ShouldRemoveInteraction_WhenSameInteractionExists() {
        User interactingUser = new User();
        interactingUser.setId(2L);
    
        Review review = new Review();
        review.setId(1L);
        review.setUser(user);
        review.setLikes(1);
        review.setDislikes(0);
    
        UserReviewInteraction existingInteraction = new UserReviewInteraction();
        existingInteraction.setUserid(interactingUser.getId());
        existingInteraction.setReview(review);
        existingInteraction.setInteraction(ReviewInteraction.LIKE);
    
        UserReviewInteractionDTO interactionDTO = new UserReviewInteractionDTO();
        interactionDTO.setInteraction(ReviewInteraction.LIKE);
        interactionDTO.setReview(review);
    
        when(userRepository.findById(2L)).thenReturn(Optional.of(interactingUser));
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(userReviewInteractionRepository.findByUseridAndReview(interactingUser.getId(), review))
            .thenReturn(Optional.of(existingInteraction));
    
        reviewService.likeOrDislikeReview(2L, interactionDTO);
    
        assertEquals(0, review.getLikes(), "The review's like count should be decremented");
        verify(userReviewInteractionRepository, times(1)).delete(existingInteraction);
        verify(reviewRepository, times(1)).save(review);  
    }
}