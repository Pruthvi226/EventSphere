package com.eventsphere.service.impl;

import com.eventsphere.dto.FeedbackDTO;
import com.eventsphere.entity.Feedback;
import com.eventsphere.entity.Event;
import com.eventsphere.entity.User;
import com.eventsphere.repository.FeedbackRepository;
import com.eventsphere.repository.EventRepository;
import com.eventsphere.repository.UserRepository;
import com.eventsphere.service.FeedbackService;
import com.eventsphere.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FeedbackServiceImpl implements FeedbackService {
    
    @Autowired
    private FeedbackRepository feedbackRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public FeedbackDTO submitFeedback(FeedbackDTO feedbackDTO) {
        Event event = eventRepository.findById(feedbackDTO.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        User student = userRepository.findById(feedbackDTO.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        if (getFeedbackByEventAndStudent(event.getId(), student.getId()).isPresent()) {
            throw new IllegalArgumentException("You have already submitted feedback for this event");
        }
        
        Feedback feedback = new Feedback();
        feedback.setRating(feedbackDTO.getRating());
        feedback.setComment(feedbackDTO.getComment());
        feedback.setSubmittedAt(LocalDateTime.now());
        feedback.setEvent(event);
        feedback.setStudent(student);
        
        Feedback saved = feedbackRepository.save(feedback);
        return mapToDTO(saved);
    }
    
    @Override
    public Optional<FeedbackDTO> getFeedbackById(Long id) {
        return feedbackRepository.findById(id).map(this::mapToDTO);
    }
    
    @Override
    public List<FeedbackDTO> getFeedbackByEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        return feedbackRepository.findByEvent(event).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<FeedbackDTO> getFeedbackByStudent(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        return feedbackRepository.findByStudent(student).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<FeedbackDTO> getFeedbackByEventAndStudent(Long eventId, Long studentId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        return feedbackRepository.findByEventAndStudent(event, student).map(this::mapToDTO);
    }
    
    @Override
    public Double getAverageRatingByEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        Double average = feedbackRepository.getAverageRatingByEvent(event);
        return average != null ? average : 0.0;
    }
    
    @Override
    public long getFeedbackCountByEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        return feedbackRepository.countByEvent(event);
    }
    
    @Override
    public void deleteFeedback(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found"));
        feedbackRepository.delete(feedback);
    }
    
    private FeedbackDTO mapToDTO(Feedback feedback) {
        FeedbackDTO dto = new FeedbackDTO();
        dto.setId(feedback.getId());
        dto.setRating(feedback.getRating());
        dto.setComment(feedback.getComment());
        dto.setSubmittedAt(feedback.getSubmittedAt());
        dto.setEventId(feedback.getEvent().getId());
        dto.setEventTitle(feedback.getEvent().getTitle());
        dto.setStudentId(feedback.getStudent().getId());
        dto.setStudentName(feedback.getStudent().getFullName());
        dto.setCreatedAt(feedback.getCreatedAt());
        return dto;
    }
}
