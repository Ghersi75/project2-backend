package com.team2.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team2.backend.enums.NotificationType;
import com.team2.backend.models.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long>{
    List<Notification> findByUserId(Long userId);

    List<Notification> findByUserIdAndType(Long userId, NotificationType type);
    
}
