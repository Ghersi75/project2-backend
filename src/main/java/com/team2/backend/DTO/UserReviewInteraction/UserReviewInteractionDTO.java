package com.team2.backend.DTO.UserReviewInteraction;

import com.team2.backend.Enums.ReviewInteraction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserReviewInteractionDTO {
    private Long userId;       // Only include the user ID
    private Long reviewId;     // Only include the review ID
    private ReviewInteraction interaction;
}