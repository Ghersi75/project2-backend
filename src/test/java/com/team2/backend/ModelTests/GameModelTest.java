package com.team2.backend.ModelTests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.ArrayList;

import com.team2.backend.dto.Game.*;
import com.team2.backend.models.*;

public class GameModelTest {

    @Test
    void testGameInitialization() {
        String steamid = "123456";
        String title = "Test Game";
        String description = "This is a test description.";

        Game game = new Game();
        game.setSteamid(steamid);
        game.setTitle(title);
        game.setDescription(description);

        assertNotNull(game);
        assertEquals(steamid, game.getSteamid());
        assertEquals(title, game.getTitle());
        assertEquals(description, game.getDescription());
    }

    @Test
    void testAddReviewToGame() {
        Game game = new Game();
        Review review1 = new Review();
        review1.setContent("Great game!");
        Review review2 = new Review();
        review2.setContent("Not bad.");

        List<Review> reviews = new ArrayList<>();
        reviews.add(review1);
        reviews.add(review2);

        game.setReviews(reviews);

        assertNotNull(game.getReviews());
        assertEquals(2, game.getReviews().size());
        assertEquals("Great game!", game.getReviews().get(0).getContent());
        assertEquals("Not bad.", game.getReviews().get(1).getContent());
    }

    @Test
    void testGameDTOConstructor() {
        String steamid = "123456";
        String title = "Test Game";
        String description = "This is a test description.";
        GameDTO gameDTO = new GameDTO(steamid, title, description);

        Game game = new Game(gameDTO);

        assertNotNull(game);
        assertEquals(gameDTO.getSteamid(), game.getSteamid());
        assertEquals(gameDTO.getTitle(), game.getTitle());
        assertEquals(gameDTO.getDescription(), game.getDescription());
    }

    @Test
    void testSetAndGetFields() {
        Game game = new Game();
        game.setId(1L);
        game.setSteamid("123456");
        game.setTitle("Test Title");
        game.setDescription("Test Description");

        assertEquals(1L, game.getId());
        assertEquals("123456", game.getSteamid());
        assertEquals("Test Title", game.getTitle());
        assertEquals("Test Description", game.getDescription());
    }
}