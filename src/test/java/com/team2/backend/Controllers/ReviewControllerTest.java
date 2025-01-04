package com.team2.backend.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.backend.DTO.Review.*;
import com.team2.backend.DTO.UserReviewInteraction.*;
import com.team2.backend.Enums.ReviewInteraction;
import com.team2.backend.Models.Review;
import com.team2.backend.Service.ReviewService;
import com.team2.backend.Kafka.Producer.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private ReviewInteractionProducer reviewInteractionProducer;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void addReview_ShouldReturnCreatedReview() throws Exception {
        NewReviewDTO newReviewDTO = new NewReviewDTO();
        newReviewDTO.setContent("Great game!");
        newReviewDTO.getGame().setId(1L);

        Review mockReview = new Review();
        mockReview.setId(1L);
        mockReview.setContent("Great game!");

        when(reviewService.addReview(anyLong(), any(NewReviewDTO.class))).thenReturn(mockReview);

        mockMvc.perform(post("/reviews/{userId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(newReviewDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Great game!"))
                .andExpect(jsonPath("$.id").value(1));

        verify(reviewService, times(1)).addReview(anyLong(), any(NewReviewDTO.class));
    }

}