package com.team2.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.team2.backend.enums.NotificationType;
import com.team2.backend.enums.ReviewInteraction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "notifications")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  
    
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude

    private User user;
    private Long reviewId;  
    private Integer appId;
    private String gameName;
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

  

    
}
