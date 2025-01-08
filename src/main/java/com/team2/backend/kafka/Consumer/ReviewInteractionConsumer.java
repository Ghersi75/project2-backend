package com.team2.backend.kafka.Consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.backend.dto.userreviewinteraction.ProducerInteractionDTO;
import com.team2.backend.dto.userreviewinteraction.UserReviewInteractionDTO;
import com.team2.backend.enums.ReviewInteraction;
import com.team2.backend.service.ReviewService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReviewInteractionConsumer {
    @Autowired
    private ReviewService reviewService;



    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "${kafka.topic.review-interaction}", groupId = "game-forum-group")
    public void consumeReviewInteraction(String message) {
        try {
            ProducerInteractionDTO interactionDTO = objectMapper.readValue(message, ProducerInteractionDTO.class);

            handleReviewInteraction(interactionDTO);
            

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleReviewInteraction(ProducerInteractionDTO interactionDTO) {
        System.out.println("Processing review interaction: " + interactionDTO);

        String username = interactionDTO.getUsername();
        reviewService.likeOrDislikeReview(username, interactionDTO);

        // Example logic
        if (interactionDTO.getInteraction() == ReviewInteraction.LIKE) {
            System.out.println(
                    "Review " + interactionDTO.getReviewid() + " liked by user " + interactionDTO.getUsername());
        } else if (interactionDTO.getInteraction() == ReviewInteraction.DISLIKE) {
            System.out.println(
                    "Review " + interactionDTO.getReviewid() + " disliked by user " + interactionDTO.getUsername());
        }

        // Further processing (e.g., updating analytics or notifying users)
    }
}