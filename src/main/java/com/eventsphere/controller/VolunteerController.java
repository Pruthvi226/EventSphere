package com.eventsphere.controller;

import com.eventsphere.service.VolunteerAssignmentService;
import com.eventsphere.service.AttendanceService;
import com.eventsphere.service.EventRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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

    @Autowired
    private EventRegistrationService registrationService;
    
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

    @PostMapping("/mark-attendance")
    public String markAttendanceById(@RequestParam Long registrationId,
                                     RedirectAttributes redirectAttributes) {
        try {
            attendanceService.markAttendance(registrationId);
            redirectAttributes.addFlashAttribute("success", "Attendance marked.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/volunteer/attendance";
    }

    @PostMapping("/mark-attendance/number")
    public String markAttendanceByNumber(@RequestParam String registrationNumber,
                                         RedirectAttributes redirectAttributes) {
        try {
            Long registrationId = registrationService.getRegistrationByNumber(registrationNumber)
                    .orElseThrow(() -> new RuntimeException("Registration number not found"))
                    .getId();
            attendanceService.markAttendance(registrationId);
            redirectAttributes.addFlashAttribute("success", "Attendance marked.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/volunteer/attendance";
    }
    
    @PostMapping("/mark-attendance/{registrationId}")
    public String markAttendance(@PathVariable Long registrationId, RedirectAttributes redirectAttributes) {
        try {
            attendanceService.markAttendance(registrationId);
            redirectAttributes.addFlashAttribute("success", "Attendance marked.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/volunteer/attendance";
    }
}
