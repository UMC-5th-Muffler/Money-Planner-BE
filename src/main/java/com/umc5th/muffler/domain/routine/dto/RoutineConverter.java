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

    public static List<RoutineAll> toRoutineInfo(Slice<Routine> routineList, Map<Long, RoutineWeeklyDetailDto> weeklyDetailDtoList) {
        return routineList.stream()
                .map(routine -> {
                    RoutineAll routineAll = RoutineAll.builder()
                            .routineId(routine.getId())
                            .routineTitle(routine.getTitle())
                            .routineCost(routine.getCost())
                            .categoryIcon(routine.getCategory().getIcon())
                            .build();

                    RoutineWeeklyDetailDto weeklyDetail = weeklyDetailDtoList.get(routine.getId());
                    if (weeklyDetail != null) {
                        routineAll.setWeeklyDetail(weeklyDetail);
                    } else {
                        routineAll.setMonthlyRepeatDay(routine.getMonthlyRepeatDay());
                    }

                    return routineAll;
                })
                .collect(Collectors.toList());
    }

    public static RoutineWeeklyDetailDto getWeeklyDetail(Integer weeklyTerm, List<Integer> dayOfWeeks) {
        return RoutineWeeklyDetailDto.builder()
                .weeklyTerm(weeklyTerm)
                .weeklyRepeatDays(dayOfWeeks)
                .build();
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
