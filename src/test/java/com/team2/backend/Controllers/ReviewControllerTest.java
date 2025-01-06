package com.team2.backend.controllers;

import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.team2.backend.controllers.*;
import com.team2.backend.dto.Review.*;
import com.team2.backend.dto.UserReviewInteraction.UserReviewInteractionDTO;
import com.team2.backend.enums.ReviewInteraction;
import com.team2.backend.kafka.Producer.ReviewInteractionProducer;
import com.team2.backend.models.*;
import com.team2.backend.service.ReviewService;
import com.team2.backend.servicetests.*;

@ExtendWith(MockitoExtension.class)
public class ReviewControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    @Mock
    private ReviewInteractionProducer reviewInteractionProducer;
    
    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();
    }

    @Test
    void addReview_ShouldReturnCreatedReview() throws Exception {
        NewReviewDTO newReviewDTO = new NewReviewDTO("Great Game!",12345);

        User user = new User();
        user.setId(1L);
        Review mockReview = new Review(user,newReviewDTO);

        when(reviewService.addReview(eq(1L), any(NewReviewDTO.class))).thenReturn(mockReview);

        mockMvc.perform(post("/reviews/{userId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(newReviewDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(mockReview.getId()))
                .andExpect(jsonPath("$.content").value("Great Game!"));

        verify(reviewService, times(1)).addReview(eq(1L), any(NewReviewDTO.class));
    }

      @Test
    void deleteReview_ShouldReturnNoContent() throws Exception {
        doNothing().when(reviewService).deleteReview(anyLong(), anyLong());

        mockMvc.perform(delete("/reviews/{reviewId}/{userId}", 1L, 1L))
                .andExpect(status().isNoContent());

        verify(reviewService, times(1)).deleteReview(1L, 1L);
    }

    @Test
    void updateReview_ShouldReturnUpdatedReview() throws Exception {
        UpdateReviewDTO updateReviewDTO = new UpdateReviewDTO("Updated review content",1,2);

        Review updatedReview = new Review();
        updatedReview.setId(1L);
        updatedReview.setContent("Updated review content");

        when(reviewService.updateReview(anyLong(), anyLong(), any(UpdateReviewDTO.class)))
                .thenReturn(updatedReview);

        mockMvc.perform(put("/reviews/{userId}/{reviewId}", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(updateReviewDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated review content"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getAllReviewsByUser_ShouldReturnReviewsList() throws Exception {
        Review review1 = new Review();
        review1.setId(1L);
        review1.setContent("First review");

        Review review2 = new Review();
        review2.setId(2L);
        review2.setContent("Second review");

        List<Review> mockReviews = List.of(review1, review2);

        when(reviewService.getAllReviewsByUser(anyLong())).thenReturn(mockReviews);

        mockMvc.perform(get("/reviews/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("First review"))
                .andExpect(jsonPath("$[1].content").value("Second review"));
    }

    @Test
    void likeReview_ShouldReturnSuccessMessage() throws Exception {
        UserReviewInteractionDTO interactionDTO = new UserReviewInteractionDTO();
        interactionDTO.setReview(new Review());
        interactionDTO.setInteraction(ReviewInteraction.LIKE);

        doNothing().when(reviewService).likeOrDislikeReview(anyLong(), any(UserReviewInteractionDTO.class));

        mockMvc.perform(post("/reviews/like?userId=1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(interactionDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Review liked successfully"));

        verify(reviewInteractionProducer, times(1)).sendReviewInteraction(any(UserReviewInteractionDTO.class));
    }

    @Test
    void dislikeReview_ShouldReturnSuccessMessage() throws Exception {
        // Arrange: Create UserReviewInteractionDTO
        UserReviewInteractionDTO interactionDTO = new UserReviewInteractionDTO();
        interactionDTO.setReview(new Review());
        interactionDTO.setInteraction(ReviewInteraction.DISLIKE);

        doNothing().when(reviewService).likeOrDislikeReview(anyLong(), any(UserReviewInteractionDTO.class));

        // Act & Assert: Perform the POST request to dislike the review and assert the response
        mockMvc.perform(post("/reviews/dislike?userId=1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(interactionDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Review disliked successfully"));

        // Verify the interaction producer was called
        verify(reviewInteractionProducer, times(1)).sendReviewInteraction(any(UserReviewInteractionDTO.class));
    }

    @Test
    void getAllReviewsByGame_ShouldReturnReviewsList() throws Exception {
        // Arrange: Create mock reviews
        Review review1 = new Review();
        review1.setId(1L);
        review1.setContent("First review for game");

        Review review2 = new Review();
        review2.setId(2L);
        review2.setContent("Second review for game");

        List<Review> mockReviews = List.of(review1, review2);

        when(reviewService.getAllReviewsByGame(anyInt())).thenReturn(mockReviews);

        // Act & Assert: Perform the GET request and validate the response
        mockMvc.perform(get("/reviews/games/{gameId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("First review for game"))
                .andExpect(jsonPath("$[1].content").value("Second review for game"));
    }
}