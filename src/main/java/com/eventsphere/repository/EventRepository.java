package com.eventsphere.repository;

import com.eventsphere.entity.Event;
import com.eventsphere.entity.User;
import com.eventsphere.entity.Department;
import com.eventsphere.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByOrganizer(User organizer);
    List<Event> findByStatus(Event.EventStatus status);
    List<Event> findByDepartment(Department department);
    List<Event> findByClub(Club club);
    List<Event> findByCategory(String category);
    
    @Query("SELECT e FROM Event e WHERE e.startDateTime > CURRENT_TIMESTAMP ORDER BY e.startDateTime ASC")
    List<Event> findUpcomingEvents();
    
    @Query("SELECT e FROM Event e WHERE e.status = 'PUBLISHED' AND e.startDateTime > CURRENT_TIMESTAMP ORDER BY e.startDateTime ASC")
    List<Event> findPublishedUpcomingEvents();
    
    @Query("SELECT e FROM Event e WHERE e.startDateTime BETWEEN :start AND :end")
    List<Event> findEventsByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
