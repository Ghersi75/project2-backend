package com.team2.backend.Kafka.Consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class CommentInteractionConsumer {

    @KafkaListener(topics = "liked_disliked_comment", groupId = "game-forum-group")
    public void listenCommentInteraction(String message) {
        System.out.println("Received comment interaction: " + message);
        // logic here
    }
}