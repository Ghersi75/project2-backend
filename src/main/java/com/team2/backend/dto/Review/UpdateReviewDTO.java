package com.team2.backend.dto.Review;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReviewDTO {
    @NotEmpty(message = "Review cannot be empty")
    @Size(max = 500, message = "Review must be less than 500 characters")
    private String content;

    @Positive
    private int likes;

    @Positive
    private int dislikes;
}
