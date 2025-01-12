package com.team2.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.backend.dto.review.NewReviewDTO;
import com.team2.backend.dto.review.UpdateReviewDTO;
import com.team2.backend.dto.review.ReviewDTO;
import com.team2.backend.dto.user.UserSignUpDTO;
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

        NewReviewDTO newReviewDTO = new NewReviewDTO("Great game!","Game", 123);
        Review review = new Review();
        review.setId(1L);
        review.setUser(new User(new UserSignUpDTO("Test User","testUser","123","CONTRIBUTOR")));
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

}
