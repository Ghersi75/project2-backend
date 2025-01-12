package com.team2.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team2.backend.enums.NotificationType;
import com.team2.backend.models.Notification;
import com.team2.backend.models.User;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long>{
    List<Notification> findByUser(User user);

    List<Notification> findByUserAndType(User user, NotificationType type);

    Optional<Notification> findByUserAndReviewIdAndType(User user, Long reviewId, NotificationType type);
    
}
