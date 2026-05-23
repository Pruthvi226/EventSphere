package com.eventsphere.controller;

import com.eventsphere.dto.EventDTO;
import com.eventsphere.dto.EventRegistrationDTO;
import com.eventsphere.dto.FeedbackDTO;
import com.eventsphere.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.eventsphere.repository.UserRepository;
import com.eventsphere.entity.User;
import java.util.List;

@Controller
@RequestMapping("/organizer")
public class OrganizerController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private EventRegistrationService registrationService;
    
    @Autowired
    private FeedbackService feedbackService;
    
    @Autowired
    private CertificateService certificateService;
    
    @Autowired
    private AttendanceService attendanceService;
    
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User user = getCurrentUser();
        List<EventDTO> events = eventService.getEventsByOrganizer(user.getId());
        
        long totalEvents = events.size();
        long totalRegistrations = events.stream()
                .mapToLong(e -> eventService.getEventRegistrationCount(e.getId()))
                .sum();
        
        model.addAttribute("user", user);
        model.addAttribute("events", events);
        model.addAttribute("totalEvents", totalEvents);
        model.addAttribute("totalRegistrations", totalRegistrations);
        
        return "organizer/dashboard";
    }
    
    @GetMapping("/events/{eventId}/registrations")
    public String eventRegistrations(@PathVariable Long eventId, Model model) {
        List<EventRegistrationDTO> registrations = registrationService.getRegistrationsByEvent(eventId);
        model.addAttribute("registrations", registrations);
        model.addAttribute("eventId", eventId);
        return "organizer/registrations";
    }
    
    @GetMapping("/events/{eventId}/attendance")
    public String eventAttendance(@PathVariable Long eventId, Model model) {
        List<EventRegistrationDTO> registrations = registrationService.getRegistrationsByEvent(eventId);
        model.addAttribute("registrations", registrations);
        model.addAttribute("eventId", eventId);
        return "organizer/attendance";
    }
    
    @PostMapping("/events/{eventId}/attendance/mark/{registrationId}")
    public String markAttendance(@PathVariable Long eventId, @PathVariable Long registrationId) {
        attendanceService.markAttendance(registrationId);
        return "redirect:/organizer/events/" + eventId + "/attendance";
    }
    
    @GetMapping("/events/{eventId}/feedback")
    public String eventFeedback(@PathVariable Long eventId, Model model) {
        List<FeedbackDTO> feedbacks = feedbackService.getFeedbackByEvent(eventId);
        Double averageRating = feedbackService.getAverageRatingByEvent(eventId);
        
        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("eventId", eventId);
        return "organizer/feedback";
    }
    
    @PostMapping("/events/{eventId}/generate-certificates/{type}")
    public String generateCertificates(@PathVariable Long eventId, @PathVariable String type) {
        certificateService.generateCertificatesForEvent(eventId, type);
        return "redirect:/organizer/dashboard";
    }
}
