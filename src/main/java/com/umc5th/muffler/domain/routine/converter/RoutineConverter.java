package com.umc5th.muffler.domain.routine.converter;

import com.umc5th.muffler.domain.routine.dto.*;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.RoutineExpense;
import com.umc5th.muffler.entity.WeeklyRoutineExpenseDetail;

import java.time.DayOfWeek;

public class RoutineConverter {

    // WeeklyRoutineRequest(dto) -> RoutineExpense(entity)
    public static RoutineExpense toWeeklyRoutine(AddWeeklyRoutineRequest request, Member member) {
        RoutineExpense routineExpense = RoutineExpense.builder()
                .member(member)
                .cost(request.getCost())
                .term(request.getTerm())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .title(request.getTitle())
                .memo(request.getMemo())
                .categoryId(request.getCategoryId())
                .build();

        request.getDayOfWeek().forEach(day -> {
            WeeklyRoutineExpenseDetail detail = WeeklyRoutineExpenseDetail.builder()
                    .dayOfWeek(DayOfWeek.of(day))
                    .routineExpense(routineExpense)
                    .build();

            routineExpense.addDetail(detail);
        });

        return routineExpense;
    }

    // MonthlyRoutineRequest(dto) -> RoutineExpense(entity)
    public static RoutineExpense toMonthlyRoutine(AddMonthlyRoutineRequest request, Member member) {
        RoutineExpense monthlyRoutineExpense = RoutineExpense.builder()
                .member(member)
                .cost(request.getCost())
                .day(request.getDay())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .title(request.getTitle())
                .memo(request.getMemo())
                .categoryId(request.getCategoryId())
                .build();

        return monthlyRoutineExpense;
    }

    // RoutineExpense(entity) -> RoutineResponse(dto)
    public static AddRoutineResponse toAddRoutineResult(RoutineExpense routineExpense) {
        return AddRoutineResponse.builder()
                .routineId(routineExpense.getId())
                .build();
    }
}
