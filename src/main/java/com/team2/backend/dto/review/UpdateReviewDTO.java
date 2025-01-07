package com.team2.backend.dto.review;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReviewDTO {
    @NotEmpty(message = "Review cannot be empty")
    // 500 inclusive
    @Size(max = 500, message = "Review must be 500 or less characters")
    private String content;
}
