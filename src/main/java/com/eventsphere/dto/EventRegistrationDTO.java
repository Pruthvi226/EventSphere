package com.eventsphere.dto;

import com.eventsphere.entity.EventRegistration;
import com.eventsphere.validation.ValueOfEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRegistrationDTO {
    @Positive(message = "Registration id must be positive")
    private Long id;

    private String registrationNumber;

    private LocalDateTime registrationDate;

    @NotBlank(message = "Status is required")
    @ValueOfEnum(enumClass = EventRegistration.RegistrationStatus.class, message = "Status must be REGISTERED, CANCELLED, or WAITLISTED")
    private String status;

    @NotNull(message = "Student is required")
    @Positive(message = "Student id must be positive")
    private Long studentId;

    private String studentName;
    private String studentEmail;

    @NotNull(message = "Event is required")
    @Positive(message = "Event id must be positive")
    private Long eventId;

    private String eventTitle;

    private Integer waitlistPosition;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
