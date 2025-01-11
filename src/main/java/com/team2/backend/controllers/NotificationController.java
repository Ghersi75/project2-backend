package com.team2.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team2.backend.enums.NotificationType;
import com.team2.backend.models.Notification;
import com.team2.backend.service.NotificationService;
import com.team2.backend.service.ReviewService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/{userId}")
    public List<Notification> getUnseenNotifications(@PathVariable Long userId) {
        return notificationService.getUnseenNotifications(userId);
    }

    @GetMapping("/{userId}/Type")
    public List<Notification> getNotificationsByType(@PathVariable Long userId, @RequestParam NotificationType type) {
        return notificationService.getNotificationsByType(userId, type);
    }

    @DeleteMapping("/{notificationId}")
    public void deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
    }

    @DeleteMapping("/all/{userId}")
    public void deleteAllNotifications(@PathVariable Long userId) {
        notificationService.deleteAllNotifications(userId);
    }

}
