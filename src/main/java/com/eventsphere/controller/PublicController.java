package com.eventsphere.controller;

import com.eventsphere.service.CertificateService;
import com.eventsphere.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping
public class PublicController {
    @Autowired
    private EventService eventService;

    @Autowired
    private CertificateService certificateService;
    
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featuredEvents", eventService.getUpcomingEvents().stream().limit(3).toList());
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
    public String verifyCertificate(@RequestParam(required = false) String certificateNumber, Model model) {
        if (StringUtils.hasText(certificateNumber)) {
            boolean verified = certificateService.verifyCertificate(certificateNumber.trim());
            model.addAttribute("certificateNumber", certificateNumber.trim());
            model.addAttribute("verified", verified);
            certificateService.getCertificateByCertificateNumber(certificateNumber.trim())
                    .ifPresent(certificate -> model.addAttribute("certificate", certificate));
        }
        return "certificate/verify";
    }
}
