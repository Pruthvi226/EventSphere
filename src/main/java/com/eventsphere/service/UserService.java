package com.eventsphere.service;

import com.eventsphere.dto.UserDTO;
import com.eventsphere.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDTO register(UserDTO userDTO);
    Optional<User> findByEmail(String email);
    Optional<UserDTO> findById(Long id);
    List<UserDTO> findByRole(User.UserRole role);
    List<UserDTO> getAllUsers();
    UserDTO updateUser(UserDTO userDTO);
    void deleteUser(Long id);
    void enableUser(Long id);
    void disableUser(Long id);
    boolean existsByEmail(String email);
    void changePassword(Long userId, String oldPassword, String newPassword);
}
