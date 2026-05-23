package com.eventsphere.repository;

import com.eventsphere.entity.Feedback;
import com.eventsphere.entity.Event;
import com.eventsphere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByEvent(Event event);
    List<Feedback> findByStudent(User student);
    
    @Query("SELECT f FROM Feedback f WHERE f.event = :event AND f.student = :student")
    Optional<Feedback> findByEventAndStudent(@Param("event") Event event, @Param("student") User student);
    
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.event = :event")
    Double getAverageRatingByEvent(@Param("event") Event event);
    
    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.event = :event")
    long countByEvent(@Param("event") Event event);
}
