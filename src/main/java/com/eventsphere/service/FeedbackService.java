package com.eventsphere.service;

import com.eventsphere.dto.FeedbackDTO;
import java.util.List;
import java.util.Optional;

public interface FeedbackService {
    FeedbackDTO submitFeedback(FeedbackDTO feedbackDTO);
    Optional<FeedbackDTO> getFeedbackById(Long id);
    List<FeedbackDTO> getFeedbackByEvent(Long eventId);
    List<FeedbackDTO> getFeedbackByStudent(Long studentId);
    Optional<FeedbackDTO> getFeedbackByEventAndStudent(Long eventId, Long studentId);
    Double getAverageRatingByEvent(Long eventId);
    long getFeedbackCountByEvent(Long eventId);
    void deleteFeedback(Long id);
}
