package com.eventsphere.repository;

import com.eventsphere.entity.Certificate;
import com.eventsphere.entity.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    Optional<Certificate> findByCertificateNumber(String certificateNumber);
    List<Certificate> findByRegistration(EventRegistration registration);
    
    @Query("SELECT c FROM Certificate c WHERE c.certificateType = :type AND c.registration.event.id = :eventId")
    List<Certificate> findByTypeAndEvent(@Param("type") Certificate.CertificateType type, @Param("eventId") Long eventId);
}
