package com.umc5th.muffler.domain.routine.converter;

import com.umc5th.muffler.domain.routine.dto.*;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.Routine;
import com.umc5th.muffler.entity.WeeklyRoutineDetail;
import com.umc5th.muffler.entity.constant.RoutineType;

import java.time.DayOfWeek;

public class RoutineConverter {

    // WeeklyRoutineRequest(dto) -> Routine(entity)
    public static Routine toWeeklyRoutine(AddWeeklyRoutineRequest request, Member member) {
        Routine routine = Routine.builder()
                .member(member)
                .cost(request.getCost())
                .term(request.getTerm())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .title(request.getTitle())
                .memo(request.getMemo())
                .categoryId(request.getCategoryId())
                .routineType(RoutineType.WEEKLY)
                .build();

        request.getDayOfWeek().forEach(day -> {
            WeeklyRoutineDetail detail = WeeklyRoutineDetail.builder()
                    .dayOfWeek(DayOfWeek.of(day))
                    .routine(routine)
                    .build();

            routine.addDetail(detail);
        });

        return routine;
    }

    // MonthlyRoutineRequest(dto) -> Routine(entity)
    public static Routine toMonthlyRoutine(AddMonthlyRoutineRequest request, Member member) {
        Routine monthlyRoutine = Routine.builder()
                .member(member)
                .cost(request.getCost())
                .day(request.getDay())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .title(request.getTitle())
                .memo(request.getMemo())
                .categoryId(request.getCategoryId())
                .routineType(RoutineType.MONTHLY)
                .build();

        return monthlyRoutine;
    }
}
