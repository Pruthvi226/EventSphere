package com.eventsphere.controller;

import com.eventsphere.dto.EventRegistrationDTO;
import com.eventsphere.dto.FeedbackDTO;
import com.eventsphere.dto.CertificateDTO;
import com.eventsphere.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.eventsphere.repository.UserRepository;
import com.eventsphere.entity.User;
import java.util.ArrayList;
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
    public String registerForEvent(@PathVariable Long eventId, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser();
        try {
            registrationService.registerForEvent(eventId, user.getId());
            redirectAttributes.addFlashAttribute("success", "Successfully registered for event.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/events/" + eventId;
    }
    
    @PostMapping("/join-waitlist/{eventId}")
    public String joinWaitlist(@PathVariable Long eventId, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser();
        try {
            registrationService.registerForEvent(eventId, user.getId());
            redirectAttributes.addFlashAttribute("success", "Successfully joined waitlist.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
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

    @PostMapping("/registrations/{registrationId}/cancel")
    public String cancelRegistration(@PathVariable Long registrationId, RedirectAttributes redirectAttributes) {
        try {
            registrationService.cancelRegistration(registrationId);
            redirectAttributes.addFlashAttribute("success", "Registration cancelled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/student/my-registrations";
    }
    
    @GetMapping("/certificates")
    public String myCertificates(Model model) {
        User user = getCurrentUser();
        List<EventRegistrationDTO> registrations = registrationService.getRegistrationsByStudent(user.getId());
        List<CertificateDTO> certificates = new ArrayList<>();
        registrations.forEach(registration ->
                certificates.addAll(certificateService.getCertificatesByRegistration(registration.getId())));
        model.addAttribute("user", user);
        model.addAttribute("certificates", certificates);
        return "student/certificates";
    }
    
    @PostMapping("/submit-feedback/{eventId}")
    public String submitFeedback(@PathVariable Long eventId, 
                                 @RequestParam int rating,
                                 @RequestParam(required = false) String comment,
                                 RedirectAttributes redirectAttributes) {
        User user = getCurrentUser();
        
        FeedbackDTO feedbackDTO = new FeedbackDTO();
        feedbackDTO.setEventId(eventId);
        feedbackDTO.setStudentId(user.getId());
        feedbackDTO.setRating(rating);
        feedbackDTO.setComment(comment);
        
        try {
            feedbackService.submitFeedback(feedbackDTO);
            redirectAttributes.addFlashAttribute("success", "Feedback submitted. Thank you.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/events/" + eventId;
    }
}
