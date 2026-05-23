package com.eventsphere.validation;

import com.eventsphere.dto.CertificateDTO;
import com.eventsphere.dto.ChangePasswordDTO;
import com.eventsphere.dto.EventDTO;
import com.eventsphere.dto.LoginDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class DtoValidationTest {
    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    @Test
    void loginRequiresValidEmailAndPassword() {
        LoginDTO loginDTO = new LoginDTO("not-an-email", "");

        Set<ConstraintViolation<LoginDTO>> violations = validator.validate(loginDTO);

        assertThat(propertyNames(violations)).contains("email", "password");
    }

    @Test
    void changePasswordRequiresMatchingConfirmation() {
        ChangePasswordDTO dto = new ChangePasswordDTO("old-password", "new-password", "different-password");

        Set<ConstraintViolation<ChangePasswordDTO>> violations = validator.validate(dto);

        assertThat(propertyNames(violations)).contains("confirmPassword");
        assertThat(violations)
                .anyMatch(violation -> violation.getMessage().equals("New password and confirm password must match"));
    }

    @Test
    void eventScheduleRejectsEndBeforeStartAndLateRegistrationDeadline() {
        LocalDateTime start = LocalDateTime.of(2026, 6, 10, 10, 0);
        EventDTO eventDTO = validEventDTO();
        eventDTO.setStartDateTime(start);
        eventDTO.setEndDateTime(start.minusHours(1));
        eventDTO.setRegistrationDeadline(start.plusMinutes(1));

        Set<ConstraintViolation<EventDTO>> violations = validator.validate(eventDTO);

        assertThat(propertyNames(violations)).contains("endDateTime", "registrationDeadline");
    }

    @Test
    void eventAcceptsCaseInsensitiveStatusEnumValue() {
        EventDTO eventDTO = validEventDTO();
        eventDTO.setStatus("published");

        Set<ConstraintViolation<EventDTO>> violations = validator.validate(eventDTO);

        assertThat(violations).isEmpty();
    }

    @Test
    void certificateTypeMustUseSupportedEnumValue() {
        CertificateDTO certificateDTO = new CertificateDTO();
        certificateDTO.setCertificateType("UNKNOWN");
        certificateDTO.setRegistrationId(1L);

        Set<ConstraintViolation<CertificateDTO>> violations = validator.validate(certificateDTO);

        assertThat(propertyNames(violations)).contains("certificateType");
    }

    private static EventDTO validEventDTO() {
        LocalDateTime start = LocalDateTime.of(2026, 6, 10, 10, 0);
        EventDTO eventDTO = new EventDTO();
        eventDTO.setTitle("Tech Fest");
        eventDTO.setDescription("Annual college technology festival");
        eventDTO.setVenue("Main Auditorium");
        eventDTO.setStartDateTime(start);
        eventDTO.setEndDateTime(start.plusHours(3));
        eventDTO.setRegistrationDeadline(start.minusDays(1));
        eventDTO.setCapacity(100);
        eventDTO.setCategory("Technology");
        return eventDTO;
    }

    private static Set<String> propertyNames(Set<? extends ConstraintViolation<?>> violations) {
        return violations.stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }
}
