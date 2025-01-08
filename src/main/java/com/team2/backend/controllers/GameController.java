package com.team2.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team2.backend.dto.game.NewFavoriteGameDTO;
import com.team2.backend.models.Game;
import com.team2.backend.service.GameService;

import jakarta.validation.Valid;

import java.util.*;

@RestController
@RequestMapping("/game")
public class GameController {

    @Autowired
    private GameService gameService;

    @GetMapping("/favorites")
    public List<Game> getFavoriteGames(@RequestParam(name = "username") String username) {
        return gameService.getFavoriteGames(username);
    }

    @GetMapping("/favorites/{appid}")
    public Map<String, Boolean> isFavoritedGame(@RequestParam(name = "username") String username,
            @PathVariable Integer appid) {
        return Map.of("favorited", gameService.isFavoritedGame(username, appid));
    }

    @PostMapping("/favorites")
    public void addFavoriteGame(@RequestParam(name = "username") String username,
            @Valid @RequestBody NewFavoriteGameDTO newFavoriteGame) {
        gameService.addFavoriteGame(username, newFavoriteGame);
    }

    @DeleteMapping("/favorites")
    public void deleteFavoriteGame(@RequestParam(name = "username") String username,
            @RequestParam(name = "appid") Integer appid) {
        gameService.deleteFavoriteGame(username, appid);
    }

}
