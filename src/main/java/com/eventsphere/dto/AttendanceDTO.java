package com.eventsphere.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDTO {
    @Positive(message = "Attendance id must be positive")
    private Long id;

    private LocalDateTime checkInTime;

    @NotNull(message = "Attendance status is required")
    private Boolean attended;

    @NotNull(message = "Registration is required")
    @Positive(message = "Registration id must be positive")
    private Long registrationId;
    private String registrationNumber;
    private String studentName;

    private Long eventId;
    private String eventTitle;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
