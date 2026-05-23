package com.eventsphere.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private Long totalEvents;
    private Long totalRegistrations;
    private Long totalAttendance;
    private Double averageAttendanceRate;
    private Long totalUsers;
    private Long totalStudents;
    private Long totalOrganizers;
    private Long totalVolunteers;
}
