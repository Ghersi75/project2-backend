package com.team2.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.backend.enums.NotificationType;
import com.team2.backend.models.Notification;
import com.team2.backend.models.User;
import com.team2.backend.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private NotificationController notificationController;

    @Mock
    private NotificationService notificationService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();
    }

    @Test
    void testGetUnseenNotifications() throws Exception {
        String username = "testUser";
        Notification notification = new Notification(1L, new User(), 123L, 456, "GameName", username, NotificationType.LIKE);
        List<Notification> notifications = Arrays.asList(notification);

        when(notificationService.getUnseenNotifications(username)).thenReturn(notifications);

        mockMvc.perform(get("/notifications/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value(username));

        verify(notificationService, times(1)).getUnseenNotifications(username);
    }

    @Test
    void testGetNotificationsByType() throws Exception {
        String username = "testUser";
        NotificationType type = NotificationType.LIKE;
        Notification notification = new Notification(1L, new User(), 123L, 456, "GameName", username, type);
        List<Notification> notifications = Arrays.asList(notification);

        when(notificationService.getNotificationsByType(username, type)).thenReturn(notifications);

        mockMvc.perform(get("/notifications/{username}/Type", username)
                .param("type", type.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value(type.toString()));

        verify(notificationService, times(1)).getNotificationsByType(username, type);
    }

    @Test
    void testDeleteNotification() throws Exception {
        Long notificationId = 1L;

        doNothing().when(notificationService).deleteNotification(notificationId);

        mockMvc.perform(delete("/notifications/{notificationId}", notificationId))
                .andExpect(status().isOk());

        verify(notificationService, times(1)).deleteNotification(notificationId);
    }

    @Test
    void testDeleteAllNotifications() throws Exception {
        String username = "testUser";

        doNothing().when(notificationService).deleteAllNotifications(username);

        mockMvc.perform(delete("/notifications/all/{username}", username))
                .andExpect(status().isOk());

        verify(notificationService, times(1)).deleteAllNotifications(username);
    }
}