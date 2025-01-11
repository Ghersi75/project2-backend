package com.team2.backend.kafka.Producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.backend.dto.userreviewinteraction.ProducerInteractionDTO;

@Service
public class ReviewCreationProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic.review-creation}")
    private String reviewCreationTopic;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sendReviewCreation(ProducerInteractionDTO interactionDTO) {
        try {
            String message = objectMapper.writeValueAsString(interactionDTO);
            kafkaTemplate.send(reviewCreationTopic, message);
            System.out.println("Sent review creation to Kafka: " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
