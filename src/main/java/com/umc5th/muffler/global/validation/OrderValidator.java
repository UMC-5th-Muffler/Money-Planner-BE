package com.umc5th.muffler.global.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
@RequiredArgsConstructor
public class OrderValidator implements ConstraintValidator<ValidOrder, String> {
    @Override
    public void initialize(ValidOrder constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value.equalsIgnoreCase("DESC") || value.equalsIgnoreCase("ASC");
    }
}
