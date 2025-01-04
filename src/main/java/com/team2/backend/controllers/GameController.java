package com.team2.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team2.backend.models.Game;
import com.team2.backend.service.GameService;
import com.team2.backend.dto.Game.GameDTO;

import java.util.*;

@RestController
@RequestMapping("/game")
@CrossOrigin(origins = "http://localhost:5432", allowCredentials = "true")
public class GameController {

    @Autowired
    private GameService gameService;

    @PostMapping("/favorites")
    public ResponseEntity<String> addFavoriteGame(@RequestParam(name = "userId") Long userId,
            @RequestBody GameDTO gamedDto) {

        gameService.addFavoriteGame(userId, gamedDto);
        return ResponseEntity.ok("Favorite game added");
    }

    @DeleteMapping("/favorites")
    public ResponseEntity<String> deleteFavoriteGame(@RequestParam(name = "userId") Long userId,
            @RequestBody GameDTO gamedDto) {
        gameService.deleteFavoriteGame(userId, gamedDto);
        return ResponseEntity.ok("Favorite game removed");
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<Game>> getFavoriteGames(@RequestParam(name = "userId") Long userId){
        return ResponseEntity.ok(gameService.getFavoriteGames(userId));
    }
}
