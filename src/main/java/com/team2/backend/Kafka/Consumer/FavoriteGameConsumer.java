package com.team2.backend.Kafka.Consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.team2.backend.Model.Game;
import com.team2.backend.Service.GameService;  
public class FavoriteGameConsumer {

    @Autowired
    private GameService gameService;

    @KafkaListener(topics = "favorited_games", groupId = "game-forum-group")
    public void listenFavoritedGame(String message) {
        System.out.println("Received favorited game message: " + message);
        
        Game game = deserializeGame(message);

        if (game != null) {
            gameService.addFavorite(game);
            System.out.println("Game favorited: " + game.getTitle());
        } else {
            System.err.println("Failed to deserialize the game message.");
        }
    }

  
    private Game deserializeGame(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(message, Game.class);
        } catch (Exception e) {
            System.err.println("Error deserializing game message: " + e.getMessage());
            return null;
        }
    }
}