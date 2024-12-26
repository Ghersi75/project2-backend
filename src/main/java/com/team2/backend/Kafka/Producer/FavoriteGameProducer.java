package com.team2.backend.Kafka.Producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class FavoriteGameProducer {

    private static final String FAVORITED_GAME_TOPIC = "favorited_games";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendFavoritedGame(String gameDetails) {
        kafkaTemplate.send(FAVORITED_GAME_TOPIC, gameDetails);
    }
}