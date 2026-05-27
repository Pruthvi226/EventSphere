package com.eventsphere.service.impl;

import com.eventsphere.dto.EventRegistrationDTO;
import com.eventsphere.entity.EventRegistration;
import com.eventsphere.entity.Event;
import com.eventsphere.entity.User;
import com.eventsphere.entity.Attendance;
import com.eventsphere.entity.Waitlist;
import com.eventsphere.repository.EventRegistrationRepository;
import com.eventsphere.repository.EventRepository;
import com.eventsphere.repository.UserRepository;
import com.eventsphere.repository.AttendanceRepository;
import com.eventsphere.repository.WaitlistRepository;
import com.eventsphere.service.EventRegistrationService;
import com.eventsphere.service.NotificationService;
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

    @Autowired
    private WaitlistRepository waitlistRepository;

    @Autowired
    private NotificationService notificationService;
    
    @Override
    public EventRegistrationDTO registerForEvent(Long eventId, Long studentId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        Optional<EventRegistration> existingOpt = registrationRepository.findByEventAndStudent(event, student);
        if (existingOpt.isPresent()) {
            EventRegistration existing = existingOpt.get();
            if (existing.getStatus() == EventRegistration.RegistrationStatus.REGISTERED) {
                throw new IllegalArgumentException("Student already registered for this event");
            } else if (existing.getStatus() == EventRegistration.RegistrationStatus.WAITLISTED) {
                throw new IllegalArgumentException("Student already waitlisted for this event");
            }
        }
        
        if (!event.getStatus().equals(Event.EventStatus.PUBLISHED)) {
            throw new IllegalArgumentException("Cannot register for an unpublished event");
        }
        if (LocalDateTime.now().isAfter(event.getRegistrationDeadline())) {
            throw new IllegalArgumentException("Registration deadline has passed");
        }
        
        long registeredCount = getEventRegistrationCount(eventId);
        boolean isWaitlist = registeredCount >= event.getCapacity();
        
        EventRegistration registration;
        if (existingOpt.isPresent()) {
            registration = existingOpt.get();
            registration.setRegistrationDate(LocalDateTime.now());
        } else {
            registration = new EventRegistration();
            registration.setRegistrationNumber(generateRegistrationNumber());
            registration.setRegistrationDate(LocalDateTime.now());
            registration.setStudent(student);
            registration.setEvent(event);
        }
        
        if (isWaitlist) {
            registration.setStatus(EventRegistration.RegistrationStatus.WAITLISTED);
            EventRegistration saved = registrationRepository.save(registration);
            
            long waitlistCount = waitlistRepository.countByEvent(event);
            Waitlist w = new Waitlist();
            w.setEvent(event);
            w.setStudent(student);
            w.setPosition((int) waitlistCount + 1);
            w.setAddedDate(LocalDateTime.now());
            waitlistRepository.save(w);
            
            notificationService.createNotification(studentId, "Joined Waitlist", 
                "You have been placed on the waitlist for " + event.getTitle() + " at position #" + (waitlistCount + 1) + ".");
            
            return mapToDTO(saved);
        } else {
            registration.setStatus(EventRegistration.RegistrationStatus.REGISTERED);
            EventRegistration saved = registrationRepository.save(registration);
            
            Optional<Attendance> attOpt = attendanceRepository.findByRegistration(saved);
            if (attOpt.isEmpty()) {
                Attendance attendance = new Attendance();
                attendance.setRegistration(saved);
                attendance.setAttended(false);
                attendanceRepository.save(attendance);
            } else {
                Attendance attendance = attOpt.get();
                attendance.setAttended(false);
                attendance.setCheckInTime(null);
                attendanceRepository.save(attendance);
            }
            
            notificationService.createNotification(studentId, "Registration Confirmed", 
                "Your registration for " + event.getTitle() + " was successful. Registration ID: " + saved.getRegistrationNumber());
            
            return mapToDTO(saved);
        }
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
        
        EventRegistration.RegistrationStatus oldStatus = registration.getStatus();
        if (oldStatus == EventRegistration.RegistrationStatus.CANCELLED) {
            return;
        }
        
        registration.setStatus(EventRegistration.RegistrationStatus.CANCELLED);
        registrationRepository.save(registration);
        
        notificationService.createNotification(registration.getStudent().getId(), "Registration Cancelled", 
            "Your registration/waitlist spot for " + registration.getEvent().getTitle() + " has been cancelled.");
        
        if (oldStatus == EventRegistration.RegistrationStatus.WAITLISTED) {
            Optional<Waitlist> wOpt = waitlistRepository.findByEventAndStudent(registration.getEvent(), registration.getStudent());
            if (wOpt.isPresent()) {
                Waitlist cancelledWaitlist = wOpt.get();
                int cancelledPos = cancelledWaitlist.getPosition();
                waitlistRepository.delete(cancelledWaitlist);
                
                List<Waitlist> remaining = waitlistRepository.findByEventOrderByPosition(registration.getEvent());
                for (Waitlist w : remaining) {
                    if (w.getPosition() > cancelledPos) {
                        w.setPosition(w.getPosition() - 1);
                        waitlistRepository.save(w);
                    }
                }
            }
        }
        
        if (oldStatus == EventRegistration.RegistrationStatus.REGISTERED) {
            promoteNextFromWaitlist(registration.getEvent());
        }
    }
    
    private void promoteNextFromWaitlist(Event event) {
        List<Waitlist> waitlistQueue = waitlistRepository.findByEventOrderByPosition(event);
        if (!waitlistQueue.isEmpty()) {
            Waitlist firstInQueue = waitlistQueue.get(0);
            User nextStudent = firstInQueue.getStudent();
            
            Optional<EventRegistration> regOpt = registrationRepository.findByEventAndStudent(event, nextStudent);
            if (regOpt.isPresent()) {
                EventRegistration reg = regOpt.get();
                reg.setStatus(EventRegistration.RegistrationStatus.REGISTERED);
                registrationRepository.save(reg);
                
                Optional<Attendance> attOpt = attendanceRepository.findByRegistration(reg);
                if (attOpt.isEmpty()) {
                    Attendance attendance = new Attendance();
                    attendance.setRegistration(reg);
                    attendance.setAttended(false);
                    attendanceRepository.save(attendance);
                } else {
                    Attendance attendance = attOpt.get();
                    attendance.setAttended(false);
                    attendance.setCheckInTime(null);
                    attendanceRepository.save(attendance);
                }
                
                notificationService.createNotification(nextStudent.getId(), "Promoted from Waitlist", 
                    "Good news! You have been promoted from the waitlist and registered for " + event.getTitle() + ".");
            }
            
            waitlistRepository.delete(firstInQueue);
            
            for (int i = 1; i < waitlistQueue.size(); i++) {
                Waitlist w = waitlistQueue.get(i);
                w.setPosition(i);
                waitlistRepository.save(w);
            }
        }
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
        Optional<EventRegistration> reg = registrationRepository.findByEventAndStudent(event, student);
        return reg.isPresent() && (reg.get().getStatus() == EventRegistration.RegistrationStatus.REGISTERED 
                || reg.get().getStatus() == EventRegistration.RegistrationStatus.WAITLISTED);
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
        
        if (registration.getStatus() == EventRegistration.RegistrationStatus.WAITLISTED) {
            waitlistRepository.findByEventAndStudent(registration.getEvent(), registration.getStudent())
                .ifPresent(w -> dto.setWaitlistPosition(w.getPosition()));
        }
        
        return dto;
    }
}
