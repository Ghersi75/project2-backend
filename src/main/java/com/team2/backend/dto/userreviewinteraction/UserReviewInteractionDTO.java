package com.team2.backend.dto.userreviewinteraction;

import com.team2.backend.enums.ReviewInteraction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserReviewInteractionDTO {
    private Long userid;
    private Long reviewid;
    private ReviewInteraction interaction;
}
