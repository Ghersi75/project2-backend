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

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/{username}")
    public List<Notification> getUnseenNotifications(@PathVariable String username) {
        return notificationService.getUnseenNotifications(username);
    }

    @GetMapping("/{username}/Type")
    public List<Notification> getNotificationsByType(@PathVariable String username, @RequestParam NotificationType type) {
        return notificationService.getNotificationsByType(username, type);
    }

    @DeleteMapping("/{notificationId}")
    public void deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
    }

    @DeleteMapping("/all/{username}")
    public void deleteAllNotifications(@PathVariable String username) {
        notificationService.deleteAllNotifications(username);
    }

}
