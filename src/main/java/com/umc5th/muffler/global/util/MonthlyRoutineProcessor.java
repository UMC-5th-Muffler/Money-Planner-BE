package com.umc5th.muffler.global.util;

import com.umc5th.muffler.entity.Routine;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MonthlyRoutineProcessor implements RoutineProcessor {
    @Override
    public boolean isRoutineDay(LocalDate date, Routine routine) {
        return date.getDayOfMonth() == routine.getMonthlyRepeatDay();
    }

    @Override
    public List<LocalDate> getRoutineDates(LocalDate startDate, LocalDate endDate, Routine routine) {
        List<LocalDate> result = new ArrayList<>();
        Integer repeatDay = routine.getMonthlyRepeatDay();
        LocalDate date = startDate.plusMonths(1).withDayOfMonth(1);

        while (!date.isAfter(endDate)) {
            if (date.lengthOfMonth() < repeatDay) {
                date = date.plusMonths(1);
                continue;
            }
            LocalDate routineDate = date.withDayOfMonth(repeatDay);
            if (routineDate.isAfter(endDate)) {
                break;
            }
            result.add(routineDate);
            date = date.plusMonths(1);
        }

        return result;
    }
}
