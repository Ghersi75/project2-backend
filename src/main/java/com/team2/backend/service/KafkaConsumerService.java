package com.team2.backend.Service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    @KafkaListener(topics = "reviews", groupId = "game-forum-group")
    public void listen(String message) {
        System.out.println("Received message: " + message);
        // Process the message
    }
}
