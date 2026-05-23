package com.eventsphere.service.impl;

import com.eventsphere.dto.UserDTO;
import com.eventsphere.entity.User;
import com.eventsphere.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void registerCreatesEnabledStudentWithEncodedPassword() {
        UserDTO input = new UserDTO();
        input.setFullName("Asha Sharma");
        input.setEmail("asha@example.com");
        input.setPassword("secret123");

        when(userRepository.existsByEmail("asha@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded-secret");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(7L);
            return savedUser;
        });

        UserDTO result = userService.register(input);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getFullName()).isEqualTo("Asha Sharma");
        assertThat(savedUser.getEmail()).isEqualTo("asha@example.com");
        assertThat(savedUser.getPassword()).isEqualTo("encoded-secret");
        assertThat(savedUser.getRole()).isEqualTo(User.UserRole.STUDENT);
        assertThat(savedUser.getEnabled()).isTrue();
        assertThat(result.getId()).isEqualTo(7L);
        assertThat(result.getPassword()).isNull();
    }

    @Test
    void registerRejectsDuplicateEmail() {
        UserDTO input = new UserDTO();
        input.setEmail("asha@example.com");

        when(userRepository.existsByEmail("asha@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already exists");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePasswordRejectsWrongCurrentPassword() {
        User user = new User();
        user.setId(3L);
        user.setPassword("encoded-old-password");

        when(userRepository.findById(3L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-old-password")).thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword(3L, "wrong-password", "new-password"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Current password is incorrect");

        verify(userRepository, never()).save(any(User.class));
    }
}
