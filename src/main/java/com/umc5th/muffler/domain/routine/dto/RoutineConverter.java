package com.umc5th.muffler.domain.routine.dto;

import com.umc5th.muffler.entity.Routine;
import com.umc5th.muffler.entity.WeeklyRepeatDay;
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

    public static List<RoutineDetailDto> toRoutineInfo(Slice<Routine> routineList, Map<Long, RoutineWeeklyDetailDto> weeklyDetailDtoList) {

        return routineList.stream()
                .map(routine -> {
                    RoutineDetailDto.RoutineDetailDtoBuilder builder = RoutineDetailDto.builder()
                            .routineId(routine.getId())
                            .routineTitle(routine.getTitle())
                            .routineMemo(routine.getMemo())
                            .routineCost(routine.getCost())
                            .categoryIcon(routine.getCategory().getIcon())
                            .categoryName(routine.getCategory().getName());

                    if (weeklyDetailDtoList.containsKey(routine.getId())) {
                        builder.weeklyDetail(weeklyDetailDtoList.get(routine.getId()));
                    } else {
                        builder.monthlyRepeatDay(routine.getMonthlyRepeatDay());
                    }

                    return builder.build();
                })
                .collect(Collectors.toList());
    }

    public static RoutineWeeklyDetailDto getWeeklyDetail(Routine routine) {

        List<Integer> dayOfWeeks = routine.getWeeklyRepeatDays()
                .stream()
                .map(weeklyRepeatDay -> weeklyRepeatDay.getDayOfWeek().getValue())
                .collect(Collectors.toList());

        return RoutineWeeklyDetailDto.builder()
                .weeklyTerm(routine.getWeeklyTerm())
                .weeklyRepeatDays(dayOfWeeks)
                .build();
    }

    public static RoutineResponse toRoutineResponse(List<RoutineDetailDto> routineList, Boolean hasNext) {
        return RoutineResponse.builder()
                .routineList(routineList)
                .hasNext(hasNext)
                .build();
    }

}
