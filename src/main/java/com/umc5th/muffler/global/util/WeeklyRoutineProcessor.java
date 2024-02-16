package com.umc5th.muffler.global.util;

import com.umc5th.muffler.entity.Routine;
import com.umc5th.muffler.entity.WeeklyRepeatDay;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
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

        LocalDate startDate = routine.getStartDate();
        int term = routine.getWeeklyTerm();

        LocalDate firstRepeatStartDate = startDate.plusWeeks(term).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        long weeksSinceStart = ChronoUnit.WEEKS.between(firstRepeatStartDate, date);
        return weeksSinceStart % term == 0;
    }

    @Override
    public List<LocalDate> getRoutineDates(LocalDate startDate, LocalDate endDate, Routine routine) {
        List<LocalDate> result = new ArrayList<>();
        int weeklyTerm = routine.getWeeklyTerm();
        List<DayOfWeek> routineDayOfWeeks = getDayOfWeeks(routine);

        LocalDate firstRepeatStartDate = startDate.plusWeeks(weeklyTerm).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        long numberOfDays = ChronoUnit.DAYS.between(firstRepeatStartDate, endDate) + 1;

        Stream.iterate(firstRepeatStartDate, date -> date.plusDays(1))
                .limit(numberOfDays)
                .forEach(date -> {
                    if (isRoutineDay(firstRepeatStartDate, weeklyTerm, date, routineDayOfWeeks)) {
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
