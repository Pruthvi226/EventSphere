package com.eventsphere.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    @Positive(message = "Notification id must be positive")
    private Long id;

    @NotBlank(message = "Notification title is required")
    @Size(max = 255, message = "Notification title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Notification message is required")
    @Size(max = 5000, message = "Notification message must not exceed 5000 characters")
    private String message;

    private Boolean readStatus;

    @NotNull(message = "User is required")
    @Positive(message = "User id must be positive")
    private Long userId;

    private LocalDateTime createdAt;
}
