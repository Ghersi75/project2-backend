package com.team2.backend.Kafka.Consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.backend.DTO.UserReviewInteraction.UserReviewInteractionDTO;
import com.team2.backend.Enums.ReviewInteraction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
public class ReviewInteractionConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "${kafka.topic.review-interaction}", groupId = "game-forum-group")
    public void consumeReviewInteraction(String message) {
        try {
            UserReviewInteractionDTO interactionDTO = objectMapper.readValue(message, UserReviewInteractionDTO.class);
            handleReviewInteraction(interactionDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleReviewInteraction(UserReviewInteractionDTO interactionDTO) {
        System.out.println("Processing review interaction: " + interactionDTO);

        // Example logic
        if (interactionDTO.getInteraction() == ReviewInteraction.LIKE) {
            System.out.println("Review " + interactionDTO.getReviewId() + " liked by user " + interactionDTO.getUserId());
        } else if (interactionDTO.getInteraction() == ReviewInteraction.DISLIKE) {
            System.out.println("Review " + interactionDTO.getReviewId() + " disliked by user " + interactionDTO.getUserId());
        }

        // Further processing (e.g., updating analytics or notifying users)
    }
}
