package com.team2.backend.servicetests;

import com.team2.backend.enums.NotificationType;
import com.team2.backend.exceptions.InvalidCredentialsException;
import com.team2.backend.models.Notification;
import com.team2.backend.models.User;
import com.team2.backend.repository.NotificationRepository;
import com.team2.backend.repository.UserRepository;
import com.team2.backend.service.*;
import com.team2.backend.dto.user.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        UserSignUpDTO userSignUpDTO = new UserSignUpDTO("testUser", "Test User", "password", "CONTRIBUTOR");
        user = new User(userSignUpDTO); 
    }

    @Test
    void testGetUnseenNotifications_Success() {
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setUser(user);
        notification.setReviewId(123L);
        notification.setAppId(456);
        notification.setGameName("Test Game");
        notification.setUsername("testUser");
        notification.setType(NotificationType.REVIEW);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(notificationRepository.findByUser(user)).thenReturn(Collections.singletonList(notification));

        List<Notification> notifications = notificationService.getUnseenNotifications("testUser");

        assertNotNull(notifications);
        assertEquals(1, notifications.size());
        Notification retrievedNotification = notifications.get(0);
        assertEquals("Test Game", retrievedNotification.getGameName());
        assertEquals("testUser", retrievedNotification.getUsername());
        assertEquals(NotificationType.REVIEW, retrievedNotification.getType());

        verify(userRepository, times(1)).findByUsername("testUser");
        verify(notificationRepository, times(1)).findByUser(user);
    }

    @Test
    void testGetUnseenNotifications_UserNotFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () ->
                notificationService.getUnseenNotifications("testUser"));
    }

    @Test
    void testDeleteNotification_Success() {
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setUser(user);

        doNothing().when(notificationRepository).deleteById(1L);

        notificationService.deleteNotification(1L);

        verify(notificationRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteAllNotifications_Success() {
        Notification notification1 = new Notification();
        Notification notification2 = new Notification();
        notification1.setUser(user);
        notification2.setUser(user);

        List<Notification> notifications = List.of(notification1, notification2);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(notificationRepository.findByUser(user)).thenReturn(notifications);
        doNothing().when(notificationRepository).deleteAll(notifications);

        notificationService.deleteAllNotifications("testUser");

        verify(notificationRepository, times(1)).deleteAll(notifications);
    }

    @Test
    void testGetNotificationsByType_Success() {
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setUser(user);
        notification.setReviewId(123L);
        notification.setAppId(456);
        notification.setGameName("Test Game");
        notification.setUsername("testUser");
        notification.setType(NotificationType.REVIEW);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        when(notificationRepository.findByUserAndType(user, NotificationType.REVIEW)).thenReturn(Collections.singletonList(notification));

        List<Notification> notifications = notificationService.getNotificationsByType("testUser", NotificationType.REVIEW);

        assertNotNull(notifications);
        assertEquals(1, notifications.size());
        Notification retrievedNotification = notifications.get(0);
        assertEquals(NotificationType.REVIEW, retrievedNotification.getType());

        verify(userRepository, times(1)).findByUsername("testUser");
        verify(notificationRepository, times(1)).findByUserAndType(user, NotificationType.REVIEW);
    }
}