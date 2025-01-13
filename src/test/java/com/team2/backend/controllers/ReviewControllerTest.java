package com.team2.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.backend.dto.review.NewReviewDTO;
import com.team2.backend.dto.review.UpdateReviewDTO;
import com.team2.backend.dto.review.ReviewDTO;
import com.team2.backend.dto.review.ReviewWithLikedDTO;
import com.team2.backend.dto.user.UserSignUpDTO;
import com.team2.backend.dto.userreviewinteraction.UserInteractionResultDTO;
import com.team2.backend.dto.userreviewinteraction.UserReviewInteractionDTO;
import com.team2.backend.enums.ReviewInteraction;
import com.team2.backend.enums.UserRole;
import com.team2.backend.kafka.Producer.ReviewCreationProducer;
import com.team2.backend.kafka.Producer.ReviewInteractionProducer;
import com.team2.backend.service.ReviewService;
import com.team2.backend.models.Review;
import com.team2.backend.models.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.security.core.Authentication;

import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private ReviewController reviewController;

    @Mock
    private ReviewService reviewService;

    @Mock
    private ReviewCreationProducer reviewCreationProducer;

    @Mock
    private ReviewInteractionProducer reviewInteractionProducer;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();
    }

    private void setAuthentication(String username) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testAddReview_Success() throws Exception {
        String username = "testUser";
        setAuthentication(username); // Set authentication here

        NewReviewDTO newReviewDTO = new NewReviewDTO("Great Game!", "Game", 123);
        Review review = new Review();
        review.setId(1L);
        review.setUser(new User(new UserSignUpDTO("Test User", "testUser", "123", "CONTRIBUTOR")));
        review.setContent("Great game!");
        review.setLikes(0);
        review.setDislikes(0);
        review.setPostedAt(OffsetDateTime.now());

        ReviewDTO reviewDTO = new ReviewDTO(review);

        when(reviewService.addReview(eq(username), eq(newReviewDTO))).thenReturn(reviewDTO);

        mockMvc.perform(post("/reviews/{username}", username)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newReviewDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(1))
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.displayName").value("Test User"))
                .andExpect(jsonPath("$.content").value("Great game!"))
                .andExpect(jsonPath("$.likes").value(0))
                .andExpect(jsonPath("$.dislikes").value(0))
                .andExpect(jsonPath("$.postedAt").exists());

        verify(reviewService, times(1)).addReview(eq(username), eq(newReviewDTO));
    }

    @Test
    void testDeleteReview_Success() throws Exception {
        String username = "testUser";
        Long reviewId = 1L;

        doNothing().when(reviewService).deleteReview(username, reviewId);

        mockMvc.perform(delete("/reviews/{username}/{reviewId}", username, reviewId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(reviewService, times(1)).deleteReview(username, reviewId);
    }

    @Test
    void testUpdateReview_Success() throws Exception {
        String username = "testUser";
        Long reviewId = 1L;
        UpdateReviewDTO updateReviewDTO = new UpdateReviewDTO("Updated content");

        doNothing().when(reviewService).updateReview(eq(username), eq(reviewId), eq(updateReviewDTO));

        mockMvc.perform(put("/reviews/{username}/{reviewId}", username, reviewId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReviewDTO)))
                .andExpect(status().isOk());

        verify(reviewService, times(1)).updateReview(eq(username), eq(reviewId), eq(updateReviewDTO));
    }

    @Test
    void testGetAllReviewsByUser() throws Exception {
        String username = "testUser";
        User user = new User(1L, "Test User", username, "password", UserRole.CONTRIBUTOR, null, null, null);
        List<Review> reviews = Arrays
                .asList(new Review(1L, user, 123, "Great game!", 0, 0, OffsetDateTime.now(), null));

        when(reviewService.getAllReviewsByUser(username)).thenReturn(reviews);

        mockMvc.perform(get("/reviews/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].content").value("Great game!"))
                .andExpect(jsonPath("$[0].appid").value(123));

        verify(reviewService, times(1)).getAllReviewsByUser(username);
    }

    @Test
    void testLikeReview() throws Exception {
        String username = "testUser";
        UserReviewInteractionDTO interactionDTO = new UserReviewInteractionDTO(1L, 123, "GameName",
                ReviewInteraction.LIKE);
        UserInteractionResultDTO resultDTO = new UserInteractionResultDTO(new Review());

        when(reviewService.likeOrDislikeReview(username, interactionDTO)).thenReturn(resultDTO);

        mockMvc.perform(post("/reviews/like")
                .param("username", username)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(interactionDTO)))
                .andExpect(status().isOk());

        verify(reviewService, times(1)).likeOrDislikeReview(username, interactionDTO);
        verify(reviewInteractionProducer, times(1)).sendReviewInteraction(any());
    }

    @Test
    void testDislikeReview() throws Exception {
        String username = "testUser";
        UserReviewInteractionDTO interactionDTO = new UserReviewInteractionDTO(1L, 123, "GameName",
                ReviewInteraction.DISLIKE);
        UserInteractionResultDTO resultDTO = new UserInteractionResultDTO(new Review());

        when(reviewService.likeOrDislikeReview(username, interactionDTO)).thenReturn(resultDTO);

        mockMvc.perform(post("/reviews/dislike")
                .param("username", username)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(interactionDTO)))
                .andExpect(status().isOk());

        verify(reviewService, times(1)).likeOrDislikeReview(username, interactionDTO);
        verify(reviewInteractionProducer, times(1)).sendReviewInteraction(any());
    }

    @Test
    void testGetAllReviewsByGame() throws Exception {
        Integer appid = 123;
        String username = "testUser";

        Review review = new Review();
        review.setId(1L);
        review.setContent("Great game!");
        review.setLikes(10);
        review.setDislikes(2);
        review.setPostedAt(OffsetDateTime.now());

        User user = new User();
        user.setUsername(username);
        user.setDisplayName("Test User");
        review.setUser(user);

        ReviewWithLikedDTO reviewDTO = new ReviewWithLikedDTO(review, ReviewInteraction.LIKE);

        List<ReviewWithLikedDTO> reviews = Arrays.asList(reviewDTO);

        Authentication auth = new UsernamePasswordAuthenticationToken(username, null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(reviewService.getAllReviewsByGame(appid, username)).thenReturn(reviews);
        mockMvc.perform(get("/reviews/games/{appid}", appid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reviewId").value(review.getId()))
                .andExpect(jsonPath("$[0].username").value(username))
                .andExpect(jsonPath("$[0].displayName").value("Test User"))
                .andExpect(jsonPath("$[0].content").value("Great game!"))
                .andExpect(jsonPath("$[0].likes").value(10))
                .andExpect(jsonPath("$[0].dislikes").value(2))
                .andExpect(jsonPath("$[0].likedByUser").value(true))
                .andExpect(jsonPath("$[0].dislikedByUser").value(false));

        verify(reviewService, times(1)).getAllReviewsByGame(appid, username);
    }

}
