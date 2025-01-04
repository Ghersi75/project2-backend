package com.team2.backend.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.team2.backend.exceptions.InvalidFavoriteGameException;
import com.team2.backend.exceptions.UserNotFoundException;
import com.team2.backend.models.Game;
import com.team2.backend.models.User;
import com.team2.backend.repository.GameRepository;
import com.team2.backend.repository.UserRepository;
import com.team2.backend.service.GameService;
import com.team2.backend.exceptions.GameNotFoundException;
import com.team2.backend.dto.Game.GameDTO;

import java.util.*;

public class GameServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addFavoriteGame_ShouldAddGameToUserFavorites() {
        Long userId = 1L;
        GameDTO gameDTO = new GameDTO();
        gameDTO.setSteamid("123");

        User user = new User();
        user.setId(userId);
        user.setFavoriteGames(new ArrayList<>());

        Game game = new Game();
        game.setSteamid("123");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(gameRepository.findBySteamid("123")).thenReturn(Optional.of(game));

        gameService.addFavoriteGame(userId, gameDTO);

        assertTrue(user.getFavoriteGames().contains(game));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void addFavoriteGame_ShouldCreateNewGameIfNotFound() {
        Long userId = 1L;
        GameDTO gameDTO = new GameDTO();
        gameDTO.setSteamid("123");

        User user = new User();
        user.setId(userId);
        user.setFavoriteGames(new ArrayList<>());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(gameRepository.findBySteamid("123")).thenReturn(Optional.empty());
        when(gameRepository.save(any(Game.class))).thenReturn(new Game(gameDTO));

        gameService.addFavoriteGame(userId, gameDTO);

        assertEquals(1, user.getFavoriteGames().size());
        verify(gameRepository, times(1)).save(any(Game.class));
    }

    @Test
    void addFavoriteGame_ShouldThrowException_WhenGameIsAlreadyFavorite() {
        Long userId = 1L;
        GameDTO gameDTO = new GameDTO();
        gameDTO.setSteamid("123");

        User user = new User();
        user.setId(userId);
        Game game = new Game();
        game.setSteamid("123");
        user.setFavoriteGames(Collections.singletonList(game));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(gameRepository.findBySteamid("123")).thenReturn(Optional.of(game));

        InvalidFavoriteGameException exception = assertThrows(
                InvalidFavoriteGameException.class,
                () -> gameService.addFavoriteGame(userId, gameDTO));
        assertEquals("Game is already in the user's favorite list.", exception.getMessage());
    }

    @Test
    void deleteFavoriteGame_ShouldRemoveGameFromUserFavorites() {
        Long userId = 1L;
        GameDTO gameDTO = new GameDTO();
        gameDTO.setSteamid("123");

        User user = new User();
        user.setId(userId);
        Game game = new Game();
        game.setSteamid("123");

        user.setFavoriteGames(new ArrayList<>());
        user.getFavoriteGames().add(game);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(gameRepository.findBySteamid("123")).thenReturn(Optional.of(game));
        gameService.deleteFavoriteGame(userId, gameDTO);

        assertTrue(user.getFavoriteGames().isEmpty(), "The game should be removed from the user's favorites.");

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void deleteFavoriteGame_ShouldThrowException_WhenGameIsNotFavorite() {
        Long userId = 1L;
        GameDTO gameDTO = new GameDTO();
        gameDTO.setSteamid("123");

        User user = new User();
        user.setId(userId);
        user.setFavoriteGames(new ArrayList<>());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(gameRepository.findBySteamid("123")).thenReturn(Optional.of(new Game()));

        InvalidFavoriteGameException exception = assertThrows(
                InvalidFavoriteGameException.class,
                () -> gameService.deleteFavoriteGame(userId, gameDTO));
        assertEquals("Game is not in the user's favorite list.", exception.getMessage());
    }

    @Test
    void deleteFavoriteGame_ShouldThrowException_WhenGameNotFound() {
        Long userId = 1L;
        GameDTO gameDTO = new GameDTO();
        gameDTO.setSteamid("123");

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(gameRepository.findBySteamid("123")).thenReturn(Optional.empty());

        GameNotFoundException exception = assertThrows(
                GameNotFoundException.class,
                () -> gameService.deleteFavoriteGame(userId, gameDTO));
        assertEquals("Game not found", exception.getMessage());
    }

    @Test
    void getFavoriteGames_ShouldReturnUserFavorites() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        Game game1 = new Game();
        game1.setSteamid("123");
        Game game2 = new Game();
        game2.setSteamid("456");
        user.setFavoriteGames(Arrays.asList(game1, game2));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        List<Game> favoriteGames = gameService.getFavoriteGames(userId);

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