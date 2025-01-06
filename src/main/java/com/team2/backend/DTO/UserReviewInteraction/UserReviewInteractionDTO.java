package com.team2.backend.DTO.UserReviewInteraction;

import com.team2.backend.Enums.ReviewInteraction;
import com.team2.backend.Models.Review;
import com.team2.backend.Models.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserReviewInteractionDTO {
    private Long userid;
    private Review review;
    private ReviewInteraction interaction;
}
