package com.eventsphere.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "certificates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String certificateNumber;

    @Column(nullable = false)
    private LocalDateTime issueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CertificateType certificateType;

    @Column(length = 500)
    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_id", nullable = false)
    private EventRegistration registration;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (issueDate == null) {
            issueDate = LocalDateTime.now();
        }
    }

    public enum CertificateType {
        PARTICIPATION, WINNER, VOLUNTEER
    }
}
