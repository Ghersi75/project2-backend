package com.team2.backend.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.team2.backend.dto.user.UserSignUpDTO;
import com.team2.backend.enums.UserRole;

@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String displayName;

    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private UserRole userRole;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Review> reviews;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<UserReviewInteraction> reviewInteractions;

    @ElementCollection
    @Column(name = "favorite_games")
    private List<Integer> favoriteGames = new ArrayList<>();

    public User(UserSignUpDTO userInfo) {
        this.username = userInfo.getUsername();
        this.displayName = userInfo.getDisplayName();
        this.username = userInfo.getUsername();
        this.password = userInfo.getPassword();
        this.userRole = userInfo.getRole() == null ? UserRole.CONTRIBUTOR : UserRole.fromString(userInfo.getRole());
        this.favoriteGames = new ArrayList<>();
    }
}
