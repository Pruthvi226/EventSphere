package com.eventsphere.service.impl;

import com.eventsphere.dto.EventDTO;
import com.eventsphere.entity.Event;
import com.eventsphere.entity.User;
import com.eventsphere.repository.EventRepository;
import com.eventsphere.repository.UserRepository;
import com.eventsphere.repository.EventRegistrationRepository;
import com.eventsphere.repository.AttendanceRepository;
import com.eventsphere.service.EventService;
import com.eventsphere.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventServiceImpl implements EventService {
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EventRegistrationRepository registrationRepository;
    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Override
    public EventDTO createEvent(EventDTO eventDTO, Long organizerId) {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer not found"));
        
        Event event = new Event();
        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        event.setVenue(eventDTO.getVenue());
        event.setStartDateTime(eventDTO.getStartDateTime());
        event.setEndDateTime(eventDTO.getEndDateTime());
        event.setRegistrationDeadline(eventDTO.getRegistrationDeadline());
        event.setCapacity(eventDTO.getCapacity());
        event.setCategory(eventDTO.getCategory());
        event.setStatus(Event.EventStatus.DRAFT);
        event.setOrganizer(organizer);
        
        Event savedEvent = eventRepository.save(event);
        return mapToDTO(savedEvent);
    }
    
    @Override
    public Optional<EventDTO> getEventById(Long id) {
        return eventRepository.findById(id).map(this::mapToDTO);
    }
    
    @Override
    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<EventDTO> getPublishedEvents() {
        return eventRepository.findByStatus(Event.EventStatus.PUBLISHED).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<EventDTO> getUpcomingEvents() {
        return eventRepository.findPublishedUpcomingEvents().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<EventDTO> getEventsByOrganizer(Long organizerId) {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer not found"));
        return eventRepository.findByOrganizer(organizer).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<EventDTO> getEventsByStatus(Event.EventStatus status) {
        return eventRepository.findByStatus(status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<EventDTO> getEventsByDepartment(Long departmentId) {
        return eventRepository.findAll().stream()
                .filter(e -> e.getDepartment() != null && e.getDepartment().getId().equals(departmentId))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<EventDTO> getEventsByCategory(String category) {
        return eventRepository.findByCategory(category).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public EventDTO updateEvent(EventDTO eventDTO) {
        Event event = eventRepository.findById(eventDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        
        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        event.setVenue(eventDTO.getVenue());
        event.setStartDateTime(eventDTO.getStartDateTime());
        event.setEndDateTime(eventDTO.getEndDateTime());
        event.setRegistrationDeadline(eventDTO.getRegistrationDeadline());
        event.setCapacity(eventDTO.getCapacity());
        event.setCategory(eventDTO.getCategory());
        
        Event updated = eventRepository.save(event);
        return mapToDTO(updated);
    }
    
    @Override
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        eventRepository.delete(event);
    }
    
    @Override
    public void publishEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        event.setStatus(Event.EventStatus.PUBLISHED);
        eventRepository.save(event);
    }
    
    @Override
    public void archiveEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        event.setStatus(Event.EventStatus.ARCHIVED);
        eventRepository.save(event);
    }
    
    @Override
    public List<EventDTO> searchEvents(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return getPublishedEvents();
        }
        String normalizedKeyword = keyword.trim().toLowerCase();
        return eventRepository.findAll().stream()
                .filter(e -> e.getStatus() == Event.EventStatus.PUBLISHED)
                .filter(e -> containsIgnoreCase(e.getTitle(), normalizedKeyword)
                        || containsIgnoreCase(e.getDescription(), normalizedKeyword)
                        || containsIgnoreCase(e.getCategory(), normalizedKeyword)
                        || containsIgnoreCase(e.getVenue(), normalizedKeyword))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public long getEventRegistrationCount(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        return registrationRepository.countRegisteredByEvent(event);
    }
    
    @Override
    public long getEventAttendanceCount(Long eventId) {
        return attendanceRepository.countAttendedByEvent(eventId);
    }
    
    private EventDTO mapToDTO(Event event) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setVenue(event.getVenue());
        dto.setStartDateTime(event.getStartDateTime());
        dto.setEndDateTime(event.getEndDateTime());
        dto.setRegistrationDeadline(event.getRegistrationDeadline());
        dto.setCapacity(event.getCapacity());
        dto.setBannerImagePath(event.getBannerImagePath());
        dto.setCategory(event.getCategory());
        dto.setStatus(event.getStatus().toString());
        dto.setOrganizerName(event.getOrganizer().getFullName());
        dto.setOrganizerId(event.getOrganizer().getId());
        if (event.getDepartment() != null) {
            dto.setDepartmentId(event.getDepartment().getId());
            dto.setDepartmentName(event.getDepartment().getName());
        }
        if (event.getClub() != null) {
            dto.setClubId(event.getClub().getId());
            dto.setClubName(event.getClub().getName());
        }
        dto.setCreatedAt(event.getCreatedAt());
        dto.setUpdatedAt(event.getUpdatedAt());
        return dto;
    }

    private boolean containsIgnoreCase(String value, String normalizedKeyword) {
        return value != null && value.toLowerCase().contains(normalizedKeyword);
    }
}
