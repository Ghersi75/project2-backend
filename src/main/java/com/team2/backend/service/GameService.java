package com.team2.backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.team2.backend.Repository.UserRepository;
import com.team2.backend.Models.User;
import com.team2.backend.Exceptions.GameNotFoundException;
import com.team2.backend.Exceptions.InvalidFavoriteGameException;
import com.team2.backend.Exceptions.UserNotFoundException;

import java.util.*;

@Service
public class GameService {
    @Autowired
    private UserRepository userRepository;


    public void addFavoriteGame(Long userId, Integer appid) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getFavoriteGames().contains(appid)) {
            throw new InvalidFavoriteGameException("Game is already in the user's favorite list.");
        }

        user.getFavoriteGames().add(appid);
        userRepository.save(user);
    }

    public void deleteFavoriteGame(Long userId, Integer appid) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.getFavoriteGames().contains(appid)) {
            throw new InvalidFavoriteGameException("Game is not in the user's favorite list.");
        }
    
        user.getFavoriteGames().remove(appid);
        userRepository.save(user);
    }

    public List<Integer> getFavoriteGames(Long userId){
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));

        return user.getFavoriteGames();
    }
}
