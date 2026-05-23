package com.eventsphere.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {
    @Positive(message = "Department id must be positive")
    private Long id;

    @NotBlank(message = "Department name is required")
    @Size(min = 2, max = 255, message = "Department name must be between 2 and 255 characters")
    private String name;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;
}
