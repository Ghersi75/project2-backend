package com.team2.backend.DTO.Review;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.team2.backend.Models.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewReviewDTO {
    @NotEmpty(message = "Description cannot be empty")
    @Size(max = 500, message = "Description must be less than 500 characters")
    private String content;
    private Long gameid;
}
