package com.eventsphere.service;

import com.eventsphere.dto.EventDTO;
import com.eventsphere.entity.Event;
import com.eventsphere.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventService {
    EventDTO createEvent(EventDTO eventDTO, Long organizerId);
    Optional<EventDTO> getEventById(Long id);
    List<EventDTO> getAllEvents();
    List<EventDTO> getPublishedEvents();
    List<EventDTO> getUpcomingEvents();
    List<EventDTO> getEventsByOrganizer(Long organizerId);
    List<EventDTO> getEventsByStatus(Event.EventStatus status);
    List<EventDTO> getEventsByDepartment(Long departmentId);
    List<EventDTO> getEventsByCategory(String category);
    EventDTO updateEvent(EventDTO eventDTO);
    void deleteEvent(Long id);
    void publishEvent(Long id);
    void archiveEvent(Long id);
    List<EventDTO> searchEvents(String keyword);
    long getEventRegistrationCount(Long eventId);
    long getEventAttendanceCount(Long eventId);
}
