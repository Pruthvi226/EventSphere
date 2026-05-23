package com.eventsphere.repository;

import com.eventsphere.entity.EventRegistration;
import com.eventsphere.entity.Event;
import com.eventsphere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    List<EventRegistration> findByEvent(Event event);
    List<EventRegistration> findByStudent(User student);
    Optional<EventRegistration> findByRegistrationNumber(String registrationNumber);
    
    @Query("SELECT COUNT(er) FROM EventRegistration er WHERE er.event = :event AND er.status = 'REGISTERED'")
    long countRegisteredByEvent(@Param("event") Event event);
    
    @Query("SELECT er FROM EventRegistration er WHERE er.event = :event AND er.student = :student")
    Optional<EventRegistration> findByEventAndStudent(@Param("event") Event event, @Param("student") User student);
    
    @Query("SELECT er FROM EventRegistration er WHERE er.event = :event AND er.status = :status")
    List<EventRegistration> findByEventAndStatus(@Param("event") Event event, @Param("status") EventRegistration.RegistrationStatus status);
}
