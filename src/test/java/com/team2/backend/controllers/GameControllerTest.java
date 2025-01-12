/* package com.team2.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.backend.dto.game.NewFavoriteGameDTO;
import com.team2.backend.models.Game;
import com.team2.backend.service.GameService;
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
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(gameController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetFavoriteGames_Success() throws Exception {
        String username = "testUser";
        List<Game> favoriteGames = List.of(new Game(1, "Test Game"));

        when(gameService.getFavoriteGames(username)).thenReturn(favoriteGames);

        mockMvc.perform(get("/game/favorites").param("username", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(favoriteGames.size()))
                .andExpect(jsonPath("$[0].id").value(favoriteGames.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(favoriteGames.get(0).getName()));

        verify(gameService, times(1)).getFavoriteGames(username);
    }

    @Test
    void testIsFavoritedGame_Success() throws Exception {
        String username = "testUser";
        int appid = 123;
        boolean isFavorited = true;

        when(gameService.isFavoritedGame(username, appid)).thenReturn(isFavorited);

        mockMvc.perform(get("/game/favorites/{appid}", appid).param("username", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favorited").value(isFavorited));

        verify(gameService, times(1)).isFavoritedGame(username, appid);
    }

    @Test
    void testAddFavoriteGame_Success() throws Exception {
        String username = "testUser";
        NewFavoriteGameDTO newFavoriteGame = new NewFavoriteGameDTO(123, "Test Game");

        doNothing().when(gameService).addFavoriteGame(username, newFavoriteGame);

        mockMvc.perform(post("/game/favorites")
                .param("username", username)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newFavoriteGame)))
                .andExpect(status().isOk());

        verify(gameService, times(1)).addFavoriteGame(username, newFavoriteGame);
    }

    @Test
    void testDeleteFavoriteGame_Success() throws Exception {
        String username = "testUser";
        int appid = 123;

        doNothing().when(gameService).deleteFavoriteGame(username, appid);

        mockMvc.perform(delete("/game/favorites")
                .param("username", username)
                .param("appid", String.valueOf(appid)))
                .andExpect(status().isOk());

        verify(gameService, times(1)).deleteFavoriteGame(username, appid);
    }
} */