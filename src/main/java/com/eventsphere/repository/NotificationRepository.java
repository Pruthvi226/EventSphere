package com.eventsphere.repository;

import com.eventsphere.entity.Notification;
import com.eventsphere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser(User user);
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.readStatus = false ORDER BY n.createdAt DESC")
    List<Notification> findUnreadByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user = :user AND n.readStatus = false")
    long countUnreadByUser(@Param("user") User user);
}
