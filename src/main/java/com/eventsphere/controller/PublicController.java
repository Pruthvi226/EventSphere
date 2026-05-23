package com.eventsphere.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class PublicController {
    
    @GetMapping("/")
    public String home(Model model) {
        return "index";
    }
    
    @GetMapping("/home")
    public String homePage(Model model) {
        return "index";
    }
    
    @GetMapping("/about")
    public String about(Model model) {
        return "about";
    }
    
    @GetMapping("/contact")
    public String contact(Model model) {
        return "contact";
    }
    
    @GetMapping("/certificate-verify")
    public String verifyCertificate(Model model) {
        return "certificate/verify";
    }
}
