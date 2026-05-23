package com.eventsphere.repository;

import com.eventsphere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(User.UserRole role);
    List<User> findByEnabled(Boolean enabled);
    boolean existsByEmail(String email);
}
