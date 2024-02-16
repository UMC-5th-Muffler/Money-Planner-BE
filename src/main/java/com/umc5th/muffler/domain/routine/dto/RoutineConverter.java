package com.umc5th.muffler.domain.routine.dto;

import com.umc5th.muffler.entity.Routine;
import com.umc5th.muffler.entity.WeeklyRepeatDay;
import com.umc5th.muffler.entity.constant.RoutineType;
import org.springframework.data.domain.Slice;

import java.time.DayOfWeek;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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

    public static List<RoutineAll> toRoutineInfoList(Slice<Routine> routineList) {
        return routineList.getContent().stream()
                .map(routine -> {
                    RoutineWeeklyDetailDto weeklyDetail = null;
                    if (routine.getType() == RoutineType.WEEKLY) {
                        List<Integer> dayOfWeeks = extractDayOfWeeks(routine);
                        weeklyDetail = new RoutineWeeklyDetailDto(routine.getWeeklyTerm(), dayOfWeeks);
                    }

                    return RoutineAll.builder()
                            .routineId(routine.getId())
                            .routineTitle(routine.getTitle())
                            .routineCost(routine.getCost())
                            .categoryIcon(routine.getCategory().getIcon())
                            .monthlyRepeatDay(routine.getType() == RoutineType.MONTHLY ? routine.getMonthlyRepeatDay() : null)
                            .weeklyDetail(weeklyDetail)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private static List<Integer> extractDayOfWeeks(Routine routine) {
        return routine.getWeeklyRepeatDays().stream()
                .map(weeklyRepeatDay -> weeklyRepeatDay.getDayOfWeek().getValue())
                .collect(Collectors.toList());
    }

    public static RoutineResponse toRoutineResponse(List<RoutineAll> routineList, Boolean hasNext) {
        return RoutineResponse.builder()
                .routineList(routineList)
                .hasNext(hasNext)
                .build();
    }

    public static RoutineDetail toRoutineDetail(Routine routine) {
        return RoutineDetail.builder()
                .routineMemo(routine.getMemo())
                .categoryName(routine.getCategory().getName())
                .build();
    }

}
