package com.team2.backend.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.backend.DTO.Game.GameDTO;
import com.team2.backend.Models.Game;
import com.team2.backend.Service.GameService;
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
    void testAddFavoriteGame_Success() throws Exception {
        Long userId = 1L;
        GameDTO gameDTO = new GameDTO("12345");

        doNothing().when(gameService).addFavoriteGame(eq(userId), any(GameDTO.class));

        mockMvc.perform(post("/game/favorites")
                .param("userId", String.valueOf(userId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gameDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Favorite game added"));
    }

    @Test
    void testDeleteFavoriteGame_Success() throws Exception {
        Long userId = 1L;
        GameDTO gameDTO = new GameDTO("12345");

        doNothing().when(gameService).deleteFavoriteGame(eq(userId), any(GameDTO.class));

        mockMvc.perform(delete("/game/favorites")
                .param("userId", String.valueOf(userId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gameDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Favorite game removed"));
    }

    @Test
    void testGetFavoriteGames_Success() throws Exception {
        Long userId = 1L;
        GameDTO gameOne = new GameDTO("12345");
        GameDTO gameTwo = new GameDTO("67890");

        List<Game> favoriteGames = Arrays.asList(
                new Game(gameOne),
                new Game(gameTwo));

        when(gameService.getFavoriteGames(userId)).thenReturn(favoriteGames);

        mockMvc.perform(get("/game/favorites")
                .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Game 1"))
                .andExpect(jsonPath("$[1].title").value("Game 2"));
    }
}
