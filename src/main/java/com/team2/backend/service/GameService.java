package com.team2.backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team2.backend.Repository.UserRepository;
import com.team2.backend.Repository.GameRepository;
import com.team2.backend.Models.User;
import com.team2.backend.Models.Game;
import com.team2.backend.DTO.Game.*;
import com.team2.backend.Exceptions.*;

import java.util.*;

@Service
public class GameService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    public void addFavoriteGame(Long userId, GameDTO gameDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Game game = gameRepository.findBySteamid(gameDTO.getSteamid())
                .orElseGet(() -> {
                    Game newGame = new Game(gameDTO);
                    return gameRepository.save(newGame);
                });

        if (user.getFavoriteGames().contains(game)) {
            throw new InvalidFavoriteGameException("Game is already in the user's favorite list.");
        }

        user.getFavoriteGames().add(game);
        userRepository.save(user);
    }

    public void deleteFavoriteGame(Long userId, GameDTO gameDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    
        Game game = gameRepository.findBySteamid(gameDTO.getSteamid())
                .orElseThrow(() -> new GameNotFoundException("Game not found"));
    
        if (!user.getFavoriteGames().contains(game)) {
            throw new InvalidFavoriteGameException("Game is not in the user's favorite list.");
        }
    
        user.getFavoriteGames().remove(game);
        userRepository.save(user);
    }

    public List<Game> getFavoriteGames(Long userId){
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));

        return user.getFavoriteGames();
    }
}
