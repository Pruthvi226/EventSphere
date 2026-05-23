package com.eventsphere.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ValueOfEnumValidator implements ConstraintValidator<ValueOfEnum, String> {
    private Set<String> acceptedValues;
    private boolean allowNull;

    @Override
    public void initialize(ValueOfEnum constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
        acceptedValues = Arrays.stream(constraintAnnotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return allowNull;
        }

        return acceptedValues.contains(value.trim().toUpperCase());
    }
}
