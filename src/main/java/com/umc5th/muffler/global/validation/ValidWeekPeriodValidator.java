package com.umc5th.muffler.global.validation;

import com.umc5th.muffler.domain.expense.dto.WeekRequestParam;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.DayOfWeek;

public class ValidWeekPeriodValidator implements ConstraintValidator<ValidWeekPeriod, WeekRequestParam> {

    @Override
    public boolean isValid(WeekRequestParam request, ConstraintValidatorContext context) {
        if (request.getStartDate() == null || request.getEndDate() == null) {
            return false;
        }

        boolean isMonday = request.getStartDate().getDayOfWeek() == DayOfWeek.MONDAY;
        boolean isCorrectEndDate = request.getStartDate().plusDays(6).equals(request.getEndDate());

        return isMonday && isCorrectEndDate;
    }
}
