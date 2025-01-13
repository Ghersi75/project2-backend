package com.team2.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.backend.dto.game.NewFavoriteGameDTO;
import com.team2.backend.models.Game;
import com.team2.backend.service.GameService;
import com.team2.backend.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GameControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private GameController gameController;

    @Mock
    private GameService gameService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(gameController).build();
    }

    @Test
    void testGetFavoriteGames() throws Exception {
        String username = "testUser";
        User user = new User();
        user.setUsername(username);
        Game game1 = new Game(1L, 101, "Game1", "link1", Arrays.asList("PC", "Console"), user);
        Game game2 = new Game(2L, 102, "Game2", "link2", Arrays.asList("PC"), user);
        List<Game> games = Arrays.asList(game1, game2);

        when(gameService.getFavoriteGames(username)).thenReturn(games);

        mockMvc.perform(get("/game/favorites")
                .param("username", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Game1"))
                .andExpect(jsonPath("$[1].name").value("Game2"))
                .andExpect(jsonPath("$[0].thumbnailLink").value("link1"))
                .andExpect(jsonPath("$[0].availableOn[0]").value("PC"))
                .andExpect(jsonPath("$[0].availableOn[1]").value("Console"));

        verify(gameService, times(1)).getFavoriteGames(username);
    }

    @Test
    void testIsFavoritedGame() throws Exception {
        String username = "testUser";
        Integer appid = 101;
        when(gameService.isFavoritedGame(username, appid)).thenReturn(true);

        mockMvc.perform(get("/game/favorites/{appid}", appid)
                .param("username", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favorited").value(true));

        verify(gameService, times(1)).isFavoritedGame(username, appid);
    }

    @Test
    void testAddFavoriteGame() throws Exception {
        String username = "testUser";
        NewFavoriteGameDTO newFavoriteGame = new NewFavoriteGameDTO(103, "New Game", "link3", Arrays.asList("Mobile"));

        doNothing().when(gameService).addFavoriteGame(username, newFavoriteGame);

        mockMvc.perform(post("/game/favorites")
                .param("username", username)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newFavoriteGame)))
                .andExpect(status().isOk());

        verify(gameService, times(1)).addFavoriteGame(username, newFavoriteGame);
    }

    @Test
    void testDeleteFavoriteGame() throws Exception {
        String username = "testUser";
        Integer appid = 103;

        doNothing().when(gameService).deleteFavoriteGame(username, appid);

        mockMvc.perform(delete("/game/favorites")
                .param("username", username)
                .param("appid", appid.toString()))
                .andExpect(status().isOk());

        verify(gameService, times(1)).deleteFavoriteGame(username, appid);
    }
}
