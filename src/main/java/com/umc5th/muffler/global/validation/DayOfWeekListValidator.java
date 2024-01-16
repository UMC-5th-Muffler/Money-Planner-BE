package com.umc5th.muffler.global.validation;

import java.time.DayOfWeek;
import java.util.EnumSet;
import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DayOfWeekListValidator implements ConstraintValidator<ValidDayOfWeekList, List<DayOfWeek>> {
    @Override
    public void initialize(ValidDayOfWeekList constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<DayOfWeek> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        for (DayOfWeek day : value) {
            if (day == null || !EnumSet.allOf(DayOfWeek.class).contains(day)) {
                return false;
            }
        }
        return true;
    }
}
