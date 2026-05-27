package com.eventsphere.service.impl;

import com.eventsphere.dto.NotificationDTO;
import com.eventsphere.entity.Notification;
import com.eventsphere.entity.User;
import com.eventsphere.entity.EventRegistration;
import com.eventsphere.entity.Event;
import com.eventsphere.entity.Certificate;
import com.eventsphere.repository.NotificationRepository;
import com.eventsphere.repository.UserRepository;
import com.eventsphere.repository.EventRegistrationRepository;
import com.eventsphere.repository.EventRepository;
import com.eventsphere.repository.CertificateRepository;
import com.eventsphere.service.NotificationService;
import com.eventsphere.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRegistrationRepository eventRegistrationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CertificateRepository certificateRepository;
    
    @Override
    public NotificationDTO createNotification(Long userId, String title, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setReadStatus(false);
        notification.setUser(user);
        
        Notification saved = notificationRepository.save(notification);
        return mapToDTO(saved);
    }
    
    @Override
    public List<NotificationDTO> getNotificationsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return notificationRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<NotificationDTO> getUnreadNotificationsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return notificationRepository.findUnreadByUser(user).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public long getUnreadCountByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return notificationRepository.countUnreadByUser(user);
    }
    
    @Override
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setReadStatus(true);
        notificationRepository.save(notification);
    }
    
    @Override
    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findUnreadByUser(
                userRepository.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found")));
        unread.forEach(n -> n.setReadStatus(true));
        notificationRepository.saveAll(unread);
    }
    
    @Override
    public void deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notificationRepository.delete(notification);
    }
    
    @Override
    public void sendRegistrationConfirmation(Long registrationId) {
        EventRegistration registration = eventRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
        User user = registration.getStudent();
        String title = "Event Registration Confirmation";
        String message = "You have successfully registered for event: " + registration.getEvent().getTitle()
                + ". Your registration number is " + registration.getRegistrationNumber() + ".";
        createNotification(user.getId(), title, message);
    }

    @Override
    public void sendEventReminder(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        List<EventRegistration> registrations = eventRegistrationRepository.findByEventAndStatus(event,
                EventRegistration.RegistrationStatus.REGISTERED);
        for (EventRegistration reg : registrations) {
            String title = "Event Reminder";
            String message = "Reminder: The event '" + event.getTitle() + "' is scheduled for " +
                    event.getStartDateTime().toString() + ".";
            createNotification(reg.getStudent().getId(), title, message);
        }
    }

    @Override
    public void sendCertificateAvailableNotification(Long certificateId) {
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));
        User user = certificate.getRegistration().getStudent();
        String title = "Certificate Available";
        String message = "Your certificate for event " + certificate.getRegistration().getEvent().getTitle()
                + " is now available. Certificate Number: " + certificate.getCertificateNumber();
        createNotification(user.getId(), title, message);
    }
    
    private NotificationDTO mapToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setReadStatus(notification.getReadStatus());
        dto.setUserId(notification.getUser().getId());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}
