package com.team2.backend.Kafka.Producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NewCommentProducer {

    private static final String NEW_COMMENT_TOPIC = "new_comments_posted";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendNewComment(String commentDetails) {
        kafkaTemplate.send(NEW_COMMENT_TOPIC, commentDetails);
    }
}