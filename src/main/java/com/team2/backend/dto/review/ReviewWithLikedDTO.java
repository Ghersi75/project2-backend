package com.team2.backend.dto.review;

import java.time.OffsetDateTime;

import com.team2.backend.enums.ReviewInteraction;
import com.team2.backend.models.Review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewWithLikedDTO {
  private Long reviewId;
  private String username;
  private String displayName;
  private String content;
  private Integer likes;
  private Integer dislikes;
  private OffsetDateTime postedAt;
  private Boolean likedByUser;
  private Boolean dislikedByUser;

  public ReviewWithLikedDTO(Review review, ReviewInteraction interaction) {
    this.reviewId = review.getId();
    this.username = review.getUser().getUsername();
    this.displayName = review.getUser().getDisplayName();
    this.content = review.getContent();
    this.likes = review.getLikes();
    this.dislikes = review.getDislikes();
    this.postedAt = review.getPostedAt();
    if (interaction == null) {
      likedByUser = null;
      dislikedByUser = null;
    } else if (interaction == ReviewInteraction.LIKE) {
      likedByUser = true;
      dislikedByUser = false;
    } else if (interaction == ReviewInteraction.DISLIKE) {
      likedByUser = false;
      dislikedByUser = true;
    }
  }
}
