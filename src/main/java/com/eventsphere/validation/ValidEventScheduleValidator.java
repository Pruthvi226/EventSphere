package com.eventsphere.validation;

import com.eventsphere.dto.EventDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class ValidEventScheduleValidator implements ConstraintValidator<ValidEventSchedule, EventDTO> {
    @Override
    public boolean isValid(EventDTO eventDTO, ConstraintValidatorContext context) {
        if (eventDTO == null) {
            return true;
        }

        LocalDateTime start = eventDTO.getStartDateTime();
        LocalDateTime end = eventDTO.getEndDateTime();
        LocalDateTime deadline = eventDTO.getRegistrationDeadline();

        boolean valid = true;
        context.disableDefaultConstraintViolation();

        if (start != null && end != null && !end.isAfter(start)) {
            context.buildConstraintViolationWithTemplate("End date must be after start date")
                    .addPropertyNode("endDateTime")
                    .addConstraintViolation();
            valid = false;
        }

        if (start != null && deadline != null && deadline.isAfter(start)) {
            context.buildConstraintViolationWithTemplate("Registration deadline must be before or equal to start date")
                    .addPropertyNode("registrationDeadline")
                    .addConstraintViolation();
            valid = false;
        }

        return valid;
    }
}
