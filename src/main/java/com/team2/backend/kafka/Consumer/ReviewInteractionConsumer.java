package com.team2.backend.kafka.Consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.backend.repository.ReviewRepository;
import com.team2.backend.repository.GameRepository;
import com.team2.backend.dto.userreviewinteraction.NotificationDTO;
import com.team2.backend.dto.userreviewinteraction.ProducerInteractionDTO;
import com.team2.backend.dto.userreviewinteraction.UserReviewInteractionDTO;
import com.team2.backend.enums.NotificationType;
import com.team2.backend.enums.ReviewInteraction;
import com.team2.backend.models.Game;
import com.team2.backend.models.Notification;
import com.team2.backend.models.Review;
import com.team2.backend.models.User;
import com.team2.backend.repository.NotificationRepository;
import com.team2.backend.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.team2.backend.service.GameService;

@Service
public class ReviewInteractionConsumer {

       @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

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

        // Example logic
        if (interactionDTO.getType() == NotificationType.LIKE) {
            System.out.println(
                    "Review " + interactionDTO.getReviewid() + " liked by user " + interactionDTO.getReviewid());
                    sendNotification(interactionDTO, "Your review was liked!");
        } else if (interactionDTO.getType() == NotificationType.DISLIKE) {
            System.out.println(
                    "Review " + interactionDTO.getReviewid() + " disliked by user " + interactionDTO.getReviewid());
                    sendNotification(interactionDTO, "Your review was disliked!");

        }
        // Further processing (e.g., updating analytics or notifying users)
    }

    private void sendNotification(ProducerInteractionDTO interactionDTO, String message) {
        Notification notification = new Notification();
        Review review = reviewRepository.findById(interactionDTO.getReviewid()).get();
       Optional< User> user = userRepository.findById(review.getUser().getId());

        List<Game> games = gameRepository.findByAppId(interactionDTO.getAppid());
        
        Game game = games.get(0);

                // Check for existing notification
                Optional<Notification> existingNotification = notificationRepository.findByUserAndReviewIdAndType(
                    review.getUser(), interactionDTO.getReviewid(), interactionDTO.getType());
    
            if (existingNotification.isPresent()) {
                // Notification already exists, do not create a new one
                System.out.println("Duplicate notification detected, skipping creation.");
                return;
            }
            
        notification.setUser(review.getUser());
        notification.setGameName(game.getName());
        notification.setReviewId(interactionDTO.getReviewid());
        notification.setAppId(interactionDTO.getAppid());
        notification.setUsername(interactionDTO.getUsername()); 

        if(interactionDTO.getType() == NotificationType.LIKE) {
           notification.setType(NotificationType.LIKE);  
        }
        else {
            notification.setType(NotificationType.DISLIKE);
        }     
        
        notificationRepository.save(notification);
        
    }

}
