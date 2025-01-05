package com.team2.backend.Controllers;

import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.team2.backend.Service.*;
import com.team2.backend.DTO.Review.*;
import com.team2.backend.Models.*;
import com.team2.backend.Controllers.*;

@ExtendWith(MockitoExtension.class)
public class ReviewControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();
    }

    @Test
    void addReview_ShouldReturnCreatedReview() throws Exception {
        NewReviewDTO newReviewDTO = new NewReviewDTO("Great Game",12345);

        User user = new User();
        user.setId(1L);
        Review mockReview = new Review(user,newReviewDTO);

        when(reviewService.addReview(eq(1L), any(NewReviewDTO.class))).thenReturn(mockReview);

        mockMvc.perform(post("/reviews/{userId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(newReviewDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Great game!"));

        verify(reviewService, times(1)).addReview(eq(1L), any(NewReviewDTO.class));
    }
}