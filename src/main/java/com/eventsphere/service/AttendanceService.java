package com.eventsphere.service;

import com.eventsphere.dto.AttendanceDTO;
import java.util.List;
import java.util.Optional;

public interface AttendanceService {
    AttendanceDTO markAttendance(Long registrationId);
    Optional<AttendanceDTO> getAttendanceByRegistration(Long registrationId);
    List<AttendanceDTO> getAttendanceByEvent(Long eventId);
    long getEventAttendanceCount(Long eventId);
    double getEventAttendancePercentage(Long eventId);
    void markAttendanceManually(Long registrationId, boolean attended);
}
