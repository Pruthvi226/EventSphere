package com.eventsphere.controller;

import com.eventsphere.dto.EventDTO;
import com.eventsphere.dto.EventRegistrationDTO;
import com.eventsphere.dto.FeedbackDTO;
import com.eventsphere.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.eventsphere.repository.UserRepository;
import com.eventsphere.entity.User;
import java.time.LocalDateTime;
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
        events.forEach(event -> {
            event.setRegistrationCount(eventService.getEventRegistrationCount(event.getId()));
            event.setAttendanceCount(eventService.getEventAttendanceCount(event.getId()));
        });
        
        long totalEvents = events.size();
        long totalRegistrations = events.stream()
                .mapToLong(e -> e.getRegistrationCount() == null ? 0 : e.getRegistrationCount())
                .sum();
        
        model.addAttribute("user", user);
        model.addAttribute("events", events);
        model.addAttribute("totalEvents", totalEvents);
        model.addAttribute("totalRegistrations", totalRegistrations);
        
        return "organizer/dashboard";
    }

    @GetMapping("/events/new")
    public String newEvent(Model model) {
        EventDTO eventDTO = new EventDTO();
        LocalDateTime start = LocalDateTime.now().plusDays(7).withSecond(0).withNano(0);
        eventDTO.setStartDateTime(start);
        eventDTO.setEndDateTime(start.plusHours(2));
        eventDTO.setRegistrationDeadline(start.minusDays(1));
        eventDTO.setCapacity(100);
        eventDTO.setCategory("Technology");
        model.addAttribute("eventDTO", eventDTO);
        return "organizer/event-form";
    }

    @PostMapping("/events")
    public String createEvent(@Valid @ModelAttribute("eventDTO") EventDTO eventDTO,
                              BindingResult bindingResult,
                              @RequestParam(defaultValue = "false") boolean publish,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "organizer/event-form";
        }

        User user = getCurrentUser();
        EventDTO createdEvent = eventService.createEvent(eventDTO, user.getId());
        if (publish) {
            eventService.publishEvent(createdEvent.getId());
            redirectAttributes.addFlashAttribute("success", "Event created and published.");
        } else {
            redirectAttributes.addFlashAttribute("success", "Event saved as a draft.");
        }
        return "redirect:/organizer/dashboard";
    }

    @PostMapping("/events/{eventId}/publish")
    public String publishEvent(@PathVariable Long eventId, RedirectAttributes redirectAttributes) {
        eventService.publishEvent(eventId);
        redirectAttributes.addFlashAttribute("success", "Event published.");
        return "redirect:/organizer/dashboard";
    }

    @PostMapping("/events/{eventId}/archive")
    public String archiveEvent(@PathVariable Long eventId, RedirectAttributes redirectAttributes) {
        eventService.archiveEvent(eventId);
        redirectAttributes.addFlashAttribute("success", "Event archived.");
        return "redirect:/organizer/dashboard";
    }
    
    @GetMapping("/events/{eventId}/registrations")
    public String eventRegistrations(@PathVariable Long eventId, Model model) {
        List<EventRegistrationDTO> registrations = registrationService.getRegistrationsByEvent(eventId);
        eventService.getEventById(eventId).ifPresent(event -> model.addAttribute("event", event));
        model.addAttribute("registrations", registrations);
        model.addAttribute("eventId", eventId);
        return "organizer/registrations";
    }
    
    @GetMapping("/events/{eventId}/attendance")
    public String eventAttendance(@PathVariable Long eventId, Model model) {
        List<EventRegistrationDTO> registrations = registrationService.getRegistrationsByEvent(eventId);
        eventService.getEventById(eventId).ifPresent(event -> model.addAttribute("event", event));
        model.addAttribute("registrations", registrations);
        model.addAttribute("eventId", eventId);
        return "organizer/attendance";
    }
    
    @PostMapping("/events/{eventId}/attendance/mark/{registrationId}")
    public String markAttendance(@PathVariable Long eventId,
                                 @PathVariable Long registrationId,
                                 RedirectAttributes redirectAttributes) {
        try {
            attendanceService.markAttendance(registrationId);
            redirectAttributes.addFlashAttribute("success", "Attendance marked.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/organizer/events/" + eventId + "/attendance";
    }
    
    @GetMapping("/events/{eventId}/feedback")
    public String eventFeedback(@PathVariable Long eventId, Model model) {
        List<FeedbackDTO> feedbacks = feedbackService.getFeedbackByEvent(eventId);
        Double averageRating = feedbackService.getAverageRatingByEvent(eventId);
        
        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("averageRating", averageRating);
        eventService.getEventById(eventId).ifPresent(event -> model.addAttribute("event", event));
        model.addAttribute("eventId", eventId);
        return "organizer/feedback";
    }
    
    @PostMapping("/events/{eventId}/generate-certificates/{type}")
    public String generateCertificates(@PathVariable Long eventId, @PathVariable String type) {
        certificateService.generateCertificatesForEvent(eventId, type);
        return "redirect:/organizer/dashboard";
    }
}
