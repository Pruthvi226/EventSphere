package com.eventsphere.controller;

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
@RequestMapping("/student")
public class StudentController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EventRegistrationService registrationService;
    
    @Autowired
    private FeedbackService feedbackService;
    
    @Autowired
    private CertificateService certificateService;
    
    @Autowired
    private NotificationService notificationService;
    
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User user = getCurrentUser();
        List<EventRegistrationDTO> registrations = registrationService.getRegistrationsByStudent(user.getId());
        long unreadNotifications = notificationService.getUnreadCountByUser(user.getId());
        
        model.addAttribute("user", user);
        model.addAttribute("registrations", registrations);
        model.addAttribute("unreadNotifications", unreadNotifications);
        
        return "student/dashboard";
    }
    
    @PostMapping("/register-event/{eventId}")
    public String registerForEvent(@PathVariable Long eventId, Model model) {
        User user = getCurrentUser();
        try {
            registrationService.registerForEvent(eventId, user.getId());
            model.addAttribute("success", "Successfully registered for event");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/events/" + eventId;
    }
    
    @GetMapping("/my-registrations")
    public String myRegistrations(Model model) {
        User user = getCurrentUser();
        List<EventRegistrationDTO> registrations = registrationService.getRegistrationsByStudent(user.getId());
        model.addAttribute("registrations", registrations);
        return "student/my-registrations";
    }
    
    @GetMapping("/certificates")
    public String myCertificates(Model model) {
        User user = getCurrentUser();
        model.addAttribute("user", user);
        return "student/certificates";
    }
    
    @PostMapping("/submit-feedback/{eventId}")
    public String submitFeedback(@PathVariable Long eventId, 
                                 @RequestParam int rating,
                                 @RequestParam(required = false) String comment) {
        User user = getCurrentUser();
        
        FeedbackDTO feedbackDTO = new FeedbackDTO();
        feedbackDTO.setEventId(eventId);
        feedbackDTO.setStudentId(user.getId());
        feedbackDTO.setRating(rating);
        feedbackDTO.setComment(comment);
        
        feedbackService.submitFeedback(feedbackDTO);
        return "redirect:/events/" + eventId;
    }
}
