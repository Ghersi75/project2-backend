package com.team2.backend.modeltests;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import com.team2.backend.dto.game.NewFavoriteGameDTO;
import com.team2.backend.models.Game;
import com.team2.backend.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameTest {

    private User user;
    private Game game;
    private NewFavoriteGameDTO newFavoriteGameDTO;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        newFavoriteGameDTO = new NewFavoriteGameDTO();
        newFavoriteGameDTO.setAppId(123);
        newFavoriteGameDTO.setName("Test Game");
        newFavoriteGameDTO.setThumbnailLink("http://example.com/thumbnail.jpg");
        newFavoriteGameDTO.setAvailableOn(List.of("PC", "Console"));

        game = new Game(newFavoriteGameDTO, user);
    }

    @Test
    public void testGameCreation() {
        assertNotNull(game);
        assertEquals(123, game.getAppid());
        assertEquals("Test Game", game.getName());
        assertEquals("http://example.com/thumbnail.jpg", game.getThumbnailLink());
        assertEquals(List.of("PC", "Console"), game.getAvailableOn());
        assertEquals(user, game.getUser());
    }

    @Test
    public void testSettersAndGetters() {
        game.setAppid(456);
        game.setName("Updated Game");
        game.setThumbnailLink("http://example.com/new_thumbnail.jpg");
        game.setAvailableOn(List.of("Mobile"));

        assertEquals(456, game.getAppid());
        assertEquals("Updated Game", game.getName());
        assertEquals("http://example.com/new_thumbnail.jpg", game.getThumbnailLink());
        assertEquals(List.of("Mobile"), game.getAvailableOn());
    }
}