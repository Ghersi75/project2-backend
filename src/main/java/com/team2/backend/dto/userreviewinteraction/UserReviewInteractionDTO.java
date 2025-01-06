package com.team2.backend.dto.userreviewinteraction;

import com.team2.backend.enums.ReviewInteraction;
import com.team2.backend.models.Review;
import com.team2.backend.models.User;

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
