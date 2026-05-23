package com.eventsphere.service;

import com.eventsphere.dto.NotificationDTO;
import java.util.List;

public interface NotificationService {
    NotificationDTO createNotification(Long userId, String title, String message);
    List<NotificationDTO> getNotificationsByUser(Long userId);
    List<NotificationDTO> getUnreadNotificationsByUser(Long userId);
    long getUnreadCountByUser(Long userId);
    void markAsRead(Long notificationId);
    void markAllAsRead(Long userId);
    void deleteNotification(Long notificationId);
    void sendRegistrationConfirmation(Long registrationId);
    void sendEventReminder(Long eventId);
    void sendCertificateAvailableNotification(Long certificateId);
}
