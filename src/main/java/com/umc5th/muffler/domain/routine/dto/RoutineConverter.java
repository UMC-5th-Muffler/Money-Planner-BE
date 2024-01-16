package com.umc5th.muffler.domain.routine.dto;

import com.umc5th.muffler.entity.Routine;
import com.umc5th.muffler.entity.WeeklyRepeatDay;
import java.time.DayOfWeek;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RoutineConverter {
    public static List<WeeklyRepeatDay> getWeeklyRepeatDayEntities(Routine routine, List<DayOfWeek> weeklyRepeatDays) {
        return weeklyRepeatDays.stream()
                .map(dayOfWeek -> WeeklyRepeatDay.builder()
                        .dayOfWeek(dayOfWeek)
                        .routine(routine)
                        .build())
                .sorted(Comparator.comparing(WeeklyRepeatDay::getDayOfWeek))
                .collect(Collectors.toList());
    }
}
