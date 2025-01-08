// package com.team2.backend.controllers;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.team2.backend.controllers.GameController;
// import com.team2.backend.service.GameService;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;

// import java.util.Arrays;
// import java.util.List;

// import static org.mockito.ArgumentMatchers.eq;
// import static org.mockito.Mockito.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @ExtendWith(MockitoExtension.class)
// class GameControllerTest {

//     private MockMvc mockMvc;

//     @InjectMocks
//     private GameController gameController;

//     @Mock
//     private GameService gameService;

//     private ObjectMapper objectMapper;

//     @BeforeEach
//     void setUp() {
//         objectMapper = new ObjectMapper();
//         MockitoAnnotations.openMocks(this);
//         mockMvc = MockMvcBuilders.standaloneSetup(gameController).build();
//     }

//     @Test
//     void addFavoriteGame_ShouldReturnOk() throws Exception {
//         Long userId = 1L;
//         Integer appid = 123;

//         doNothing().when(gameService).addFavoriteGame(userId, appid);

//         mockMvc.perform(post("/game/favorites")
//                 .param("userId", String.valueOf(userId))
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(String.valueOf(appid)))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string("Favorite game added"));

//         verify(gameService, times(1)).addFavoriteGame(userId, appid);
//     }

//     @Test
//     void testDeleteFavoriteGame_Success() throws Exception {
//         Long userId = 1L;
//         int appid = 12345;

//         doNothing().when(gameService).deleteFavoriteGame(userId, appid);

//         mockMvc.perform(delete("/game/favorites")
//                 .param("userId", String.valueOf(userId))
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(appid)))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string("Favorite game removed"));
//     }

//     @Test
//     void testGetFavoriteGames_Success() throws Exception {
//         Long userId = 1L;
//         Integer[] favoriteGames = {123, 456};

//         when(gameService.getFavoriteGames(userId)).thenReturn(Arrays.asList(favoriteGames));

//         mockMvc.perform(get("/game/favorites")
//                 .param("userId", String.valueOf(userId)))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.length()").value(favoriteGames.length))
//                 .andExpect(jsonPath("$[0]").value(favoriteGames[0]))
//                 .andExpect(jsonPath("$[1]").value(favoriteGames[1]));

//         verify(gameService, times(1)).getFavoriteGames(userId);
//     }
// }
