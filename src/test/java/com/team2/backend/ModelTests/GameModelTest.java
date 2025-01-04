package com.team2.backend.ModelTests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.ArrayList;

import com.team2.backend.DTO.Game.*;
import com.team2.backend.Models.*;

public class GameModelTest {

    @Test
    void testGameInitialization() {
        String steamid = "123456";

        Game game = new Game();
        game.setAppid(steamid);


        assertNotNull(game);
        assertEquals(steamid, game.getAppid());

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
        GameDTO gameDTO = new GameDTO(steamid);

        Game game = new Game(gameDTO);

        assertNotNull(game);
        assertEquals(gameDTO.getAppid(), game.getAppid());
    }

    @Test
    void testSetAndGetFields() {
        Game game = new Game();
        game.setId(1L);
        game.setAppid("123456");

        assertEquals(1L, game.getId());
        assertEquals("123456", game.getAppid());
    }
}