package com.eventsphere.service.impl;

import com.eventsphere.dto.AttendanceDTO;
import com.eventsphere.entity.Attendance;
import com.eventsphere.entity.EventRegistration;
import com.eventsphere.repository.AttendanceRepository;
import com.eventsphere.repository.EventRegistrationRepository;
import com.eventsphere.service.AttendanceService;
import com.eventsphere.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AttendanceServiceImpl implements AttendanceService {
    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private EventRegistrationRepository registrationRepository;
    
    @Override
    public AttendanceDTO markAttendance(Long registrationId) {
        EventRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
        
        Attendance attendance = attendanceRepository.findByRegistration(registration)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found"));
        
        if (attendance.getAttended()) {
            throw new IllegalArgumentException("Attendance already marked");
        }
        
        attendance.setAttended(true);
        attendance.setCheckInTime(LocalDateTime.now());
        
        Attendance saved = attendanceRepository.save(attendance);
        return mapToDTO(saved);
    }
    
    @Override
    public Optional<AttendanceDTO> getAttendanceByRegistration(Long registrationId) {
        return attendanceRepository.findByRegistration(
                registrationRepository.findById(registrationId)
                        .orElseThrow(() -> new ResourceNotFoundException("Registration not found")))
                .map(this::mapToDTO);
    }
    
    @Override
    public List<AttendanceDTO> getAttendanceByEvent(Long eventId) {
        return attendanceRepository.findAttendedByEvent(eventId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public long getEventAttendanceCount(Long eventId) {
        return attendanceRepository.countAttendedByEvent(eventId);
    }
    
    @Override
    public double getEventAttendancePercentage(Long eventId) {
        long attended = getEventAttendanceCount(eventId);
        long total = registrationRepository.findAll().stream()
                .filter(r -> r.getEvent().getId().equals(eventId) && 
                           r.getStatus().equals(EventRegistration.RegistrationStatus.REGISTERED))
                .count();
        
        if (total == 0) return 0;
        return (double) attended / total * 100;
    }
    
    @Override
    public void markAttendanceManually(Long registrationId, boolean attended) {
        EventRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
        
        Attendance attendance = attendanceRepository.findByRegistration(registration)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found"));
        
        attendance.setAttended(attended);
        if (attended && attendance.getCheckInTime() == null) {
            attendance.setCheckInTime(LocalDateTime.now());
        }
        
        attendanceRepository.save(attendance);
    }
    
    private AttendanceDTO mapToDTO(Attendance attendance) {
        AttendanceDTO dto = new AttendanceDTO();
        dto.setId(attendance.getId());
        dto.setCheckInTime(attendance.getCheckInTime());
        dto.setAttended(attendance.getAttended());
        dto.setRegistrationId(attendance.getRegistration().getId());
        dto.setRegistrationNumber(attendance.getRegistration().getRegistrationNumber());
        dto.setStudentName(attendance.getRegistration().getStudent().getFullName());
        dto.setEventId(attendance.getRegistration().getEvent().getId());
        dto.setEventTitle(attendance.getRegistration().getEvent().getTitle());
        dto.setCreatedAt(attendance.getCreatedAt());
        dto.setUpdatedAt(attendance.getUpdatedAt());
        return dto;
    }
}
