package com.umc5th.muffler.global.util;

import com.umc5th.muffler.entity.Routine;
import com.umc5th.muffler.entity.WeeklyRepeatDay;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WeeklyRoutineProcessor implements RoutineProcessor {
    @Override
    public boolean isRoutineDay(LocalDate date, Routine routine) {
        if (!getDayOfWeeks(routine).contains(date.getDayOfWeek())) {
            return false;
        }

        long weeksSinceStart = ChronoUnit.WEEKS.between(routine.getStartDate(), date);
        return weeksSinceStart % routine.getWeeklyTerm() == 0;
    }

    @Override
    public List<LocalDate> getRoutineDates(LocalDate startDate, LocalDate endDate, Routine routine) {
        List<LocalDate> result = new ArrayList<>();
        long numberOfDays = ChronoUnit.DAYS.between(startDate, endDate);
        List<DayOfWeek> routineDayOfWeeks = getDayOfWeeks(routine);

        Stream.iterate(startDate.plusDays(1), date -> date.plusDays(1))
                .limit(numberOfDays + 1)
                .forEach(date -> {
                    if (isRoutineDay(startDate, routine.getWeeklyTerm(), date, routineDayOfWeeks)) {
                        result.add(date);
                    }
                });

        return result;
    }

    private static List<DayOfWeek> getDayOfWeeks(Routine routine) {
        return routine.getWeeklyRepeatDays().stream()
                .map(WeeklyRepeatDay::getDayOfWeek)
                .collect(Collectors.toList());
    }

    private boolean isRoutineDay(LocalDate startDate, int term, LocalDate date, List<DayOfWeek> routineDayOfWeeks) {
        if (!routineDayOfWeeks.contains(date.getDayOfWeek())) {
            return false;
        }

        long weeksSinceStart = ChronoUnit.WEEKS.between(startDate, date);
        return weeksSinceStart % term == 0;
    }
}
