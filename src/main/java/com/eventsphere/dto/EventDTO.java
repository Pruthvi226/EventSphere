package com.eventsphere.dto;

import com.eventsphere.entity.Event;
import com.eventsphere.validation.ValidEventSchedule;
import com.eventsphere.validation.ValueOfEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ValidEventSchedule
public class EventDTO {
    @Positive(message = "Event id must be positive")
    private Long id;

    @NotBlank(message = "Event title is required")
    @Size(min = 3, max = 255, message = "Event title must be between 3 and 255 characters")
    private String title;

    @NotBlank(message = "Event description is required")
    @Size(min = 10, message = "Event description must be at least 10 characters")
    private String description;

    @NotBlank(message = "Venue is required")
    @Size(max = 255, message = "Venue must not exceed 255 characters")
    private String venue;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDateTime;

    @NotNull(message = "End date is required")
    private LocalDateTime endDateTime;

    @NotNull(message = "Registration deadline is required")
    private LocalDateTime registrationDeadline;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    private String bannerImagePath;
    
    @NotBlank(message = "Category is required")
    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    @ValueOfEnum(enumClass = Event.EventStatus.class, allowNull = true, message = "Status must be DRAFT, PUBLISHED, ARCHIVED, CANCELLED, or COMPLETED")
    private String status;
    @Positive(message = "Organizer id must be positive")
    private Long organizerId;
    private String organizerName;
    @Positive(message = "Department id must be positive")
    private Long departmentId;
    private String departmentName;
    @Positive(message = "Club id must be positive")
    private Long clubId;
    private String clubName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private Long registrationCount;
    private Long attendanceCount;
}
