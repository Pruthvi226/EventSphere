package com.eventsphere.dto;

import com.eventsphere.entity.Certificate;
import com.eventsphere.validation.ValueOfEnum;
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
public class CertificateDTO {
    @Positive(message = "Certificate id must be positive")
    private Long id;

    @Size(max = 100, message = "Certificate number must not exceed 100 characters")
    private String certificateNumber;

    private LocalDateTime issueDate;

    @NotBlank(message = "Certificate type is required")
    @ValueOfEnum(enumClass = Certificate.CertificateType.class, message = "Certificate type must be PARTICIPATION, WINNER, or VOLUNTEER")
    private String certificateType;

    @Size(max = 500, message = "File path must not exceed 500 characters")
    private String filePath;

    @NotNull(message = "Registration is required")
    @Positive(message = "Registration id must be positive")
    private Long registrationId;
    private String studentName;
    private String eventTitle;

    private LocalDateTime createdAt;
}
