package com.eventsphere.repository;

import com.eventsphere.entity.VolunteerAssignment;
import com.eventsphere.entity.Event;
import com.eventsphere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VolunteerAssignmentRepository extends JpaRepository<VolunteerAssignment, Long> {
    List<VolunteerAssignment> findByEvent(Event event);
    List<VolunteerAssignment> findByVolunteer(User volunteer);
    
    @Query("SELECT va FROM VolunteerAssignment va WHERE va.event = :event AND va.volunteer = :volunteer")
    Optional<VolunteerAssignment> findByEventAndVolunteer(@Param("event") Event event, @Param("volunteer") User volunteer);
    
    @Query("SELECT COUNT(va) FROM VolunteerAssignment va WHERE va.event = :event")
    long countByEvent(@Param("event") Event event);
}
