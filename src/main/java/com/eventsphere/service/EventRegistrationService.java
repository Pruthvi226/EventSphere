package com.eventsphere.service;

import com.eventsphere.dto.EventRegistrationDTO;
import com.eventsphere.entity.EventRegistration;
import java.util.List;
import java.util.Optional;

public interface EventRegistrationService {
    EventRegistrationDTO registerForEvent(Long eventId, Long studentId);
    Optional<EventRegistrationDTO> getRegistrationById(Long id);
    Optional<EventRegistrationDTO> getRegistrationByNumber(String registrationNumber);
    List<EventRegistrationDTO> getRegistrationsByEvent(Long eventId);
    List<EventRegistrationDTO> getRegistrationsByStudent(Long studentId);
    List<EventRegistrationDTO> getRegistrationsByStatus(Long eventId, EventRegistration.RegistrationStatus status);
    void cancelRegistration(Long registrationId);
    void approveRegistration(Long registrationId);
    long getEventRegistrationCount(Long eventId);
    boolean isStudentRegistered(Long eventId, Long studentId);
    boolean canRegisterForEvent(Long eventId, Long studentId);
}
