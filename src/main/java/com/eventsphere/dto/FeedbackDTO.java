package com.eventsphere.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDTO {
    @Positive(message = "Feedback id must be positive")
    private Long id;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer rating;

    @Size(max = 5000, message = "Comment must not exceed 5000 characters")
    private String comment;

    private LocalDateTime submittedAt;

    @NotNull(message = "Event is required")
    @Positive(message = "Event id must be positive")
    private Long eventId;

    private String eventTitle;

    @NotNull(message = "Student is required")
    @Positive(message = "Student id must be positive")
    private Long studentId;

    private String studentName;

    private LocalDateTime createdAt;
}
