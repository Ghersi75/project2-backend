package com.team2.backend.servicetests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.team2.backend.dto.game.NewFavoriteGameDTO;
import com.team2.backend.exceptions.InvalidFavoriteGameException;
import com.team2.backend.exceptions.UserNotFoundException;
import com.team2.backend.models.Game;
import com.team2.backend.models.User;
import com.team2.backend.repository.GameRepository;
import com.team2.backend.repository.UserRepository;
import com.team2.backend.service.GameService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameServiceTest {

    @InjectMocks
    private GameService gameService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameRepository gameRepository;

    private User user;
    private Game game;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUsername("testuser");
        game = new Game(1L, 123, "Test Game", "http://example.com/thumbnail.jpg", List.of("PC"), user);
    }

    @Test
    public void testGetFavoriteGames_UserExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(gameRepository.findByUser(user)).thenReturn(List.of(game));

        List<Game> favoriteGames = gameService.getFavoriteGames("testuser");

        assertNotNull(favoriteGames);
        assertEquals(1, favoriteGames.size());
        assertEquals(game, favoriteGames.get(0));
    }

    @Test
    public void testGetFavoriteGames_UserNotFound() {
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            gameService.getFavoriteGames("nonexistentuser");
        });
    }

    @Test
    void testFavoriteGame_And_IsFavorited() {
        NewFavoriteGameDTO newFavoriteGameDTO = new NewFavoriteGameDTO(game.getAppId(), game.getName(), game.getThumbnailLink(), game.getAvailableOn());
    
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
    
        when(gameRepository.save(any(Game.class))).thenReturn(game);
    
        when(gameRepository.findByUserAndAppId(user, game.getAppId())).thenReturn(List.of(game));

        gameService.addFavoriteGame(user.getUsername(), newFavoriteGameDTO);
    
        boolean result = gameService.isFavoritedGame(user.getUsername(), game.getAppId());
    
        assertTrue(result, "The game should be favorited by the user");
    
        verify(gameRepository, times(1)).save(any(Game.class)); // Verify the game was saved
        verify(gameRepository, times(1)).findByUserAndAppId(user, game.getAppId()); // Verify the game was checked as favorited
    }
    

    @Test
    public void testIsFavoritedGame_NotExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(gameRepository.findByUserAndAppId(user, 123)).thenReturn(new ArrayList<>());

        assertFalse(gameService.isFavoritedGame("testuser", 123));
    }

    @Test
    public void testAddFavoriteGame_Success() {
        NewFavoriteGameDTO newGameDTO = new NewFavoriteGameDTO();
        newGameDTO.setAppId(123);
        newGameDTO.setName("New Test Game");
        newGameDTO.setThumbnailLink("http://example.com/new_thumbnail.jpg");
        newGameDTO.setAvailableOn(List.of("PC"));

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(gameRepository.findByUserAndAppId(user,123)).thenReturn(new ArrayList<>());

        assertDoesNotThrow(() -> gameService.addFavoriteGame("testuser", newGameDTO));

        verify(gameRepository, times(1)).save(any(Game.class));
    }
/* 
    @Test
    public void testAddFavoriteGame_AlreadyExists() {
        NewFavoriteGameDTO newGameDTO = new NewFavoriteGameDTO();
        newGameDTO.setAppId(123);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(gameRepository.findByUserAndAppd(user,123)).thenReturn(List.of(game));

        assertThrows(InvalidFavoriteGameException.class, () -> {
            gameService.addFavoriteGame("testuser", newGameDTO);
        });
    }

    @Test
    public void testDeleteFavoriteGame_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(gameRepository.findByUserAndAppid(user,123)).thenReturn(List.of(game));

        assertDoesNotThrow(() -> gameService.deleteFavoriteGame("testuser", 123));

        verify(gameRepository, times(1)).deleteByUserAndAppId(user, 123);
    }

    @Test
    public void testDeleteFavoriteGame_NotExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(gameRepository.findByUserAndAppid(user,123)).thenReturn(new ArrayList<>());

        assertThrows(InvalidFavoriteGameException.class, () -> {
            gameService.deleteFavoriteGame("testuser", 123);
        });
    } */
}