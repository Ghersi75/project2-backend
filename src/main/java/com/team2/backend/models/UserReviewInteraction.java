package com.team2.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.team2.backend.enums.ReviewInteraction;
import com.team2.backend.dto.UserReviewInteraction.UserReviewInteractionDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "user_review_interaction")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserReviewInteraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewInteraction interaction;
    

    public UserReviewInteraction(UserReviewInteractionDTO userreview){
        this.user = userreview.getUser();
        this.review = userreview.getReview();
        this.interaction = userreview.getInteraction();
    }
}
