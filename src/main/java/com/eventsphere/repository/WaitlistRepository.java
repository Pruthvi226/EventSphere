package com.eventsphere.repository;

import com.eventsphere.entity.Waitlist;
import com.eventsphere.entity.Event;
import com.eventsphere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WaitlistRepository extends JpaRepository<Waitlist, Long> {
    List<Waitlist> findByEventOrderByPosition(Event event);
    
    @Query("SELECT w FROM Waitlist w WHERE w.event = :event AND w.student = :student")
    Optional<Waitlist> findByEventAndStudent(@Param("event") Event event, @Param("student") User student);
    
    @Query("SELECT COUNT(w) FROM Waitlist w WHERE w.event = :event")
    long countByEvent(@Param("event") Event event);
}
