package com.eventsphere.repository;

import com.eventsphere.entity.Attendance;
import com.eventsphere.entity.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByRegistration(EventRegistration registration);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.attended = true AND a.registration.event.id = :eventId")
    long countAttendedByEvent(@Param("eventId") Long eventId);
    
    @Query("SELECT a FROM Attendance a WHERE a.registration.event.id = :eventId AND a.attended = true")
    List<Attendance> findAttendedByEvent(@Param("eventId") Long eventId);
}
