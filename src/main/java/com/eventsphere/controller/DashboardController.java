package com.eventsphere.controller;

import com.eventsphere.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.eventsphere.entity.User;
import com.eventsphere.repository.UserRepository;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private NotificationService notificationService;
    
    @GetMapping
    public String dashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        model.addAttribute("user", user);
        
        long unreadCount = notificationService.getUnreadCountByUser(user.getId());
        model.addAttribute("unreadNotifications", unreadCount);
        
        switch (user.getRole()) {
            case ADMIN:
                return "redirect:/admin/dashboard";
            case ORGANIZER:
                return "redirect:/organizer/dashboard";
            case VOLUNTEER:
                return "redirect:/volunteer/dashboard";
            case STUDENT:
                return "redirect:/student/dashboard";
            default:
                return "dashboard/home";
        }
    }
}
