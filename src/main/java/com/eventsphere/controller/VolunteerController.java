package com.eventsphere.controller;

import com.eventsphere.service.VolunteerAssignmentService;
import com.eventsphere.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.eventsphere.repository.UserRepository;
import com.eventsphere.entity.User;

@Controller
@RequestMapping("/volunteer")
public class VolunteerController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired(required = false)
    private VolunteerAssignmentService volunteerAssignmentService;
    
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
        model.addAttribute("user", user);
        return "volunteer/dashboard";
    }
    
    @GetMapping("/attendance")
    public String attendancePage(Model model) {
        User user = getCurrentUser();
        model.addAttribute("user", user);
        return "volunteer/attendance";
    }
    
    @PostMapping("/mark-attendance/{registrationId}")
    public String markAttendance(@PathVariable Long registrationId) {
        attendanceService.markAttendance(registrationId);
        return "redirect:/volunteer/attendance";
    }
}
