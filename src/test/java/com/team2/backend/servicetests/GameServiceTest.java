package com.team2.backend.servicetests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.team2.backend.exceptions.InvalidFavoriteGameException;
import com.team2.backend.exceptions.UserNotFoundException;
import com.team2.backend.models.User;
import com.team2.backend.repository.UserRepository;
import com.team2.backend.service.GameService;

import java.util.*;

public class GameServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GameService gameService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addFavoriteGame_ShouldAddGameToUserFavorites() {
        Long userId = 1L;
        int favoriteGame = 12345;

        User user = new User();
        user.setId(userId);
        user.setFavoriteGames(new ArrayList<>());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        gameService.addFavoriteGame(userId, favoriteGame);

        assertTrue(user.getFavoriteGames().contains(favoriteGame));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void addFavoriteGame_ShouldCreateNewGameIfNotFound() {
        Long userId = 1L;
        int favoriteGame = 12345;

        User user = new User();
        user.setId(userId);
        user.setFavoriteGames(new ArrayList<>());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        gameService.addFavoriteGame(userId, favoriteGame);

        assertEquals(1, user.getFavoriteGames().size());
    }

    @Test
    void addFavoriteGame_ShouldThrowException_WhenGameIsAlreadyFavorite() {
        Long userId = 1L;
        int favoriteGame = 12345;

        User user = new User();
        user.setId(userId);
        user.setFavoriteGames(Collections.singletonList(favoriteGame));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        InvalidFavoriteGameException exception = assertThrows(
                InvalidFavoriteGameException.class,
                () -> gameService.addFavoriteGame(userId, favoriteGame));
        assertEquals("Game is already in the user's favorite list.", exception.getMessage());
    }

    @Test
    void deleteFavoriteGame_ShouldRemoveGameFromUserFavorites() {
        Long userId = 1L;
        int favoriteGame = 12345;
    
        User user = new User();
        user.setId(userId);
        
        user.setFavoriteGames(new ArrayList<>());
        user.getFavoriteGames().add(favoriteGame);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        gameService.deleteFavoriteGame(userId, favoriteGame);
    
        assertTrue(user.getFavoriteGames().isEmpty(), "The game should be removed from the user's favorites.");

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void deleteFavoriteGame_ShouldThrowException_WhenGameIsNotFavorite() {
        Long userId = 1L;
        int favoriteGame = 12345;

        User user = new User();
        user.setId(userId);
        user.setFavoriteGames(new ArrayList<>());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        InvalidFavoriteGameException exception = assertThrows(
                InvalidFavoriteGameException.class,
                () -> gameService.deleteFavoriteGame(userId, favoriteGame));
        assertEquals("Game is not in the user's favorite list.", exception.getMessage());
    }

    @Test
    void getFavoriteGames_ShouldReturnUserFavorites() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        int game1 = 12345;
        int game2 = 12345;
        user.setFavoriteGames(Arrays.asList(game1, game2));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        List<Integer> favoriteGames = gameService.getFavoriteGames(userId);

        assertEquals(2, favoriteGames.size());
        assertTrue(favoriteGames.contains(game1));
        assertTrue(favoriteGames.contains(game2));
    }

    @Test
    void getFavoriteGames_ShouldThrowException_WhenUserNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> gameService.getFavoriteGames(userId));
        assertEquals("User not found", exception.getMessage());
    }
}