package com.team2.backend.dto.game;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewFavoriteGameDTO {
  private Integer appId;
  private String name;
  private String thumbnailLink;
  private List<String> availableOn;
}
