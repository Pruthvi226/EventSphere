package com.eventsphere.controller;

import com.eventsphere.dto.EventDTO;
import com.eventsphere.entity.User;
import com.eventsphere.repository.UserRepository;
import com.eventsphere.service.EventRegistrationService;
import com.eventsphere.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/events")
public class EventController {
    
    @Autowired
    private EventService eventService;

    @Autowired
    private EventRegistrationService registrationService;

    @Autowired
    private UserRepository userRepository;
    
    @GetMapping
    public String listEvents(Model model) {
        List<EventDTO> events = eventService.getPublishedEvents();
        model.addAttribute("events", events);
        return "events/list";
    }
    
    @GetMapping("/upcoming")
    public String upcomingEvents(Model model) {
        List<EventDTO> events = eventService.getUpcomingEvents();
        model.addAttribute("events", events);
        return "events/list";
    }
    
    @GetMapping("/{id}")
    public String eventDetails(@PathVariable Long id, Model model) {
        EventDTO event = eventService.getEventById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        long registrationCount = eventService.getEventRegistrationCount(id);
        long attendanceCount = eventService.getEventAttendanceCount(id);
        
        model.addAttribute("event", event);
        model.addAttribute("registrationCount", registrationCount);
        model.addAttribute("attendanceCount", attendanceCount);
        model.addAttribute("spotsAvailable", Math.max(0, event.getCapacity() - registrationCount));
        model.addAttribute("currentUser", getCurrentUser().orElse(null));

        getCurrentUser().ifPresentOrElse(user -> {
            boolean registered = user.getRole() == User.UserRole.STUDENT
                    && registrationService.isStudentRegistered(id, user.getId());
            boolean canRegister = user.getRole() == User.UserRole.STUDENT
                    && !registered
                    && registrationService.canRegisterForEvent(id, user.getId());
            model.addAttribute("registered", registered);
            model.addAttribute("canRegister", canRegister);
            
            if (registered) {
                registrationService.getRegistrationsByStudent(user.getId()).stream()
                        .filter(r -> r.getEventId().equals(id))
                        .findFirst()
                        .ifPresent(reg -> {
                            model.addAttribute("registrationStatus", reg.getStatus());
                            model.addAttribute("waitlistPosition", reg.getWaitlistPosition());
                        });
            }

            // Determine if user can join waitlist when event is full
            boolean canJoinWaitlist = false;
            if (user.getRole() == User.UserRole.STUDENT && !registered) {
                if (event.getRegistrationDeadline() != null && java.time.LocalDateTime.now().isBefore(event.getRegistrationDeadline())) {
                    long spots = Math.max(0, event.getCapacity() - registrationCount);
                    if (spots == 0) {
                        // Event full, allow waitlist registration (service will handle)
                        canJoinWaitlist = true;
                    }
                }
            }
            model.addAttribute("canJoinWaitlist", canJoinWaitlist);
        }, () -> {
            model.addAttribute("registered", false);
            model.addAttribute("canRegister", false);
            model.addAttribute("canJoinWaitlist", false);
        });
        
        return "events/details";
    }
    
    @GetMapping("/search")
    public String searchEvents(@RequestParam String keyword, Model model) {
        if (!StringUtils.hasText(keyword)) {
            return "redirect:/events";
        }
        List<EventDTO> events = eventService.searchEvents(keyword);
        model.addAttribute("events", events);
        model.addAttribute("keyword", keyword);
        return "events/list";
    }

    private Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            return Optional.empty();
        }
        return userRepository.findByEmail(authentication.getName());
    }
}
