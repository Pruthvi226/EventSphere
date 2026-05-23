package com.eventsphere.controller;

import com.eventsphere.dto.EventDTO;
import com.eventsphere.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/events")
public class EventController {
    
    @Autowired
    private EventService eventService;
    
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
        return "events/upcoming";
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
        model.addAttribute("spotsAvailable", event.getCapacity() - registrationCount);
        
        return "events/details";
    }
    
    @GetMapping("/search")
    public String searchEvents(@RequestParam String keyword, Model model) {
        List<EventDTO> events = eventService.searchEvents(keyword);
        model.addAttribute("events", events);
        model.addAttribute("keyword", keyword);
        return "events/search-results";
    }
}
