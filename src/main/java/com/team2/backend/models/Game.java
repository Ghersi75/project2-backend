package com.team2.backend.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.team2.backend.dto.game.NewFavoriteGameDTO;

import jakarta.persistence.Entity;
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

@Table(name = "games")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Game {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Integer appId;

  private String name;

  private String thumbnailLink;

  private List<String> availableOn;

  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "user_id", nullable = false)
  @ToString.Exclude
  private User user;

  public Game(NewFavoriteGameDTO newFavoriteGame, User user) {
    this.appId = newFavoriteGame.getAppId();
    this.name = newFavoriteGame.getName();
    this.thumbnailLink = newFavoriteGame.getThumbnailLink();
    this.availableOn = newFavoriteGame.getAvailableOn();
    this.user = user;
  }
}
