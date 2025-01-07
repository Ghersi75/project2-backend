package com.team2.backend.dto.review;

import java.time.OffsetDateTime;

import com.team2.backend.models.Review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {
  private Long reviewId;
  private String username;
  private String displayName;
  private String content;
  private Integer likes;
  private Integer dislikes;
  private OffsetDateTime postedAt;

  public ReviewDTO(Review review) {
    this.reviewId = review.getId();
    this.username = review.getUser().getUsername();
    this.displayName = review.getUser().getDisplayName();
    this.content = review.getContent();
    this.likes = review.getLikes();
    this.dislikes = review.getDislikes();
    this.postedAt = review.getPostedAt();
  }
}