package com.eventsphere.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "waitlist", uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "event_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Waitlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column
    private Integer position;

    @Column(nullable = false)
    private LocalDateTime addedDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (addedDate == null) {
            addedDate = LocalDateTime.now();
        }
    }
}
