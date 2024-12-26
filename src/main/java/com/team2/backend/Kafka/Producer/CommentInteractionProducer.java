package com.team2.backend.Kafka.Producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CommentInteractionProducer {

    private static final String COMMENT_INTERACTION_TOPIC = "liked_disliked_comment";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendCommentInteraction(String interactionDetails) {
        kafkaTemplate.send(COMMENT_INTERACTION_TOPIC, interactionDetails);
    }
}