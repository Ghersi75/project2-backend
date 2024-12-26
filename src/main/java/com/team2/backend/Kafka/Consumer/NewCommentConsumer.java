package com.team2.backend.Kafka.Consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NewCommentConsumer {

    @KafkaListener(topics = "new_comments_posted", groupId = "game-forum-group")
    public void listenNewComment(String message) {
        System.out.println("Received new comment: " + message);
        // logic here
    }
}