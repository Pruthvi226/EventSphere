package com.eventsphere.controller;

import com.eventsphere.dto.LoginDTO;
import com.eventsphere.dto.UserDTO;
import com.eventsphere.entity.User;
import com.eventsphere.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        return "auth/login";
    }
    
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginDTO loginDTO, 
                       BindingResult bindingResult,
                       Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/login";
        }
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Invalid email or password");
            return "auth/login";
        }
    }
    
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "auth/register";
    }
    
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute UserDTO userDTO, 
                          BindingResult bindingResult,
                          Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        
        if (userService.existsByEmail(userDTO.getEmail())) {
            bindingResult.rejectValue("email", "error.email", "Email already exists");
            return "auth/register";
        }
        
        try {
            UserDTO registered = userService.register(userDTO);
            model.addAttribute("message", "Registration successful. Please login.");
            return "redirect:/auth/login";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "auth/register";
        }
    }
    
    @PostMapping("/logout")
    public String logout() {
        SecurityContextHolder.clearContext();
        return "redirect:/";
    }
}
