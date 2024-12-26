package com.team2.backend.DTO.Game;

import java.util.List;

import com.team2.backend.Models.Review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameDTO {
    private Long id;

    private String steamid;

    private String title;

    private String description;

    private List<Review> reviews;
}
