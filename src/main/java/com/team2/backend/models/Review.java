package com.team2.backend.models;

import java.time.OffsetDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.team2.backend.dto.review.NewReviewDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "reviews")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    private int appid;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int likes = 0;

    @Column(nullable = false)
    private int dislikes = 0;

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime postedAt;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @ToString.Exclude
    private List<UserReviewInteraction> userInteractions;

    public Review(User user, NewReviewDTO newReviewDTO) {
        this.user = user;
        this.appid = newReviewDTO.getAppid();
        this.content = newReviewDTO.getContent();
        this.likes = 0;
        this.dislikes = 0;
    }
}
