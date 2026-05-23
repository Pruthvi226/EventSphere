package com.eventsphere.service.impl;

import com.eventsphere.dto.EventRegistrationDTO;
import com.eventsphere.entity.EventRegistration;
import com.eventsphere.entity.Event;
import com.eventsphere.entity.User;
import com.eventsphere.entity.Attendance;
import com.eventsphere.repository.EventRegistrationRepository;
import com.eventsphere.repository.EventRepository;
import com.eventsphere.repository.UserRepository;
import com.eventsphere.repository.AttendanceRepository;
import com.eventsphere.service.EventRegistrationService;
import com.eventsphere.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventRegistrationServiceImpl implements EventRegistrationService {
    
    @Autowired
    private EventRegistrationRepository registrationRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Override
    public EventRegistrationDTO registerForEvent(Long eventId, Long studentId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        if (isStudentRegistered(eventId, studentId)) {
            throw new IllegalArgumentException("Student already registered for this event");
        }
        
        if (!canRegisterForEvent(eventId, studentId)) {
            throw new IllegalArgumentException("Cannot register for this event");
        }
        
        EventRegistration registration = new EventRegistration();
        registration.setRegistrationNumber(generateRegistrationNumber());
        registration.setRegistrationDate(LocalDateTime.now());
        registration.setStatus(EventRegistration.RegistrationStatus.REGISTERED);
        registration.setStudent(student);
        registration.setEvent(event);
        
        EventRegistration saved = registrationRepository.save(registration);
        
        // Create attendance record
        Attendance attendance = new Attendance();
        attendance.setRegistration(saved);
        attendance.setAttended(false);
        attendanceRepository.save(attendance);
        
        return mapToDTO(saved);
    }
    
    @Override
    public Optional<EventRegistrationDTO> getRegistrationById(Long id) {
        return registrationRepository.findById(id).map(this::mapToDTO);
    }
    
    @Override
    public Optional<EventRegistrationDTO> getRegistrationByNumber(String registrationNumber) {
        return registrationRepository.findByRegistrationNumber(registrationNumber).map(this::mapToDTO);
    }
    
    @Override
    public List<EventRegistrationDTO> getRegistrationsByEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        return registrationRepository.findByEvent(event).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<EventRegistrationDTO> getRegistrationsByStudent(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        return registrationRepository.findByStudent(student).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<EventRegistrationDTO> getRegistrationsByStatus(Long eventId, EventRegistration.RegistrationStatus status) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        return registrationRepository.findByEventAndStatus(event, status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public void cancelRegistration(Long registrationId) {
        EventRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
        registration.setStatus(EventRegistration.RegistrationStatus.CANCELLED);
        registrationRepository.save(registration);
    }
    
    @Override
    public void approveRegistration(Long registrationId) {
        EventRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
        registration.setStatus(EventRegistration.RegistrationStatus.REGISTERED);
        registrationRepository.save(registration);
    }
    
    @Override
    public long getEventRegistrationCount(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        return registrationRepository.countRegisteredByEvent(event);
    }
    
    @Override
    public boolean isStudentRegistered(Long eventId, Long studentId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        return registrationRepository.findByEventAndStudent(event, student).isPresent();
    }
    
    @Override
    public boolean canRegisterForEvent(Long eventId, Long studentId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        
        if (!event.getStatus().equals(Event.EventStatus.PUBLISHED)) {
            return false;
        }
        
        if (LocalDateTime.now().isAfter(event.getRegistrationDeadline())) {
            return false;
        }
        
        long registeredCount = getEventRegistrationCount(eventId);
        return registeredCount < event.getCapacity();
    }
    
    private String generateRegistrationNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "REG-" + timestamp + "-" + uuid;
    }
    
    private EventRegistrationDTO mapToDTO(EventRegistration registration) {
        EventRegistrationDTO dto = new EventRegistrationDTO();
        dto.setId(registration.getId());
        dto.setRegistrationNumber(registration.getRegistrationNumber());
        dto.setRegistrationDate(registration.getRegistrationDate());
        dto.setStatus(registration.getStatus().toString());
        dto.setStudentId(registration.getStudent().getId());
        dto.setStudentName(registration.getStudent().getFullName());
        dto.setStudentEmail(registration.getStudent().getEmail());
        dto.setEventId(registration.getEvent().getId());
        dto.setEventTitle(registration.getEvent().getTitle());
        dto.setCreatedAt(registration.getCreatedAt());
        dto.setUpdatedAt(registration.getUpdatedAt());
        return dto;
    }
}
