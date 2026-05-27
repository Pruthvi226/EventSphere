package com.eventsphere.controller;

import com.eventsphere.service.UserService;
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
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService;
    
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User user = getCurrentUser();
        
        long totalUsers = userRepository.count();
        long totalStudents = userRepository.findByRole(User.UserRole.STUDENT).size();
        long totalOrganizers = userRepository.findByRole(User.UserRole.ORGANIZER).size();
        long totalVolunteers = userRepository.findByRole(User.UserRole.VOLUNTEER).size();
        
        model.addAttribute("user", user);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("totalOrganizers", totalOrganizers);
        model.addAttribute("totalVolunteers", totalVolunteers);
        
        return "admin/dashboard";
    }
    
    @GetMapping("/users")
    public String manageUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin/users";
    }
    
    @PostMapping("/users/{userId}/enable")
    public String enableUser(@PathVariable Long userId) {
        userService.enableUser(userId);
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/{userId}/disable")
    public String disableUser(@PathVariable Long userId) {
        userService.disableUser(userId);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{userId}/role")
    public String updateRole(@PathVariable Long userId, @RequestParam User.UserRole role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(role);
        userRepository.save(user);
        return "redirect:/admin/users";
    }
    
    @DeleteMapping("/users/{userId}")
    public String deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return "redirect:/admin/users";
    }
}
