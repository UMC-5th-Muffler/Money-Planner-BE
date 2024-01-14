package com.umc5th.muffler.domain.routine.converter;

import com.umc5th.muffler.domain.routine.dto.*;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.MonthlyRoutineExpense;
import com.umc5th.muffler.entity.WeeklyRoutineExpense;
import com.umc5th.muffler.entity.WeeklyRoutineExpenseDetail;

import java.time.DayOfWeek;

public class RoutineConverter {

    // WeeklyRoutineRequest(dto) -> WeeklyRoutineExpense(entity)
    public static WeeklyRoutineExpense toWeeklyRoutine(AddWeeklyRoutineRequest request, Member member) {
        WeeklyRoutineExpense weeklyRoutineExpense = WeeklyRoutineExpense.builder()
                .member(member)
                .cost(request.getCost())
                .term(request.getTerm())
                .endDate(request.getEndDate())
                .build();

        request.getDaysOfWeek().forEach(day -> {
            WeeklyRoutineExpenseDetail detail = WeeklyRoutineExpenseDetail.builder()
                    .dayOfWeek(DayOfWeek.of(day))
                    .weeklyRoutineExpense(weeklyRoutineExpense)
                    .build();

            weeklyRoutineExpense.addDetail(detail);
        });

        return weeklyRoutineExpense;
    }

    // WeeklyRoutineExpense(entity) -> RoutineResponse(dto)
    public static AddRoutineResponse toAddWeeklyRoutineResult(WeeklyRoutineExpense weeklyRoutineExpense) {
        return AddRoutineResponse.builder()
                .routineId(weeklyRoutineExpense.getId())
                .build();
    }

    // MonthlyRoutineRequest(dto) -> MonthlyRoutineExpense(entity)
    public static MonthlyRoutineExpense toMonthlyRoutine(AddMonthlyRoutineRequest request, Member member) {
        MonthlyRoutineExpense monthlyRoutineExpense = MonthlyRoutineExpense.builder()
                .member(member)
                .cost(request.getCost())
                .day(request.getDay())
                .endDate(request.getEndDate())
                .build();

        return monthlyRoutineExpense;
    }

    // MonthlyRoutineExpense(entity) -> RoutineResponse(dto)
    public static AddRoutineResponse toAddMonthlyRoutineResult(MonthlyRoutineExpense monthlyRoutineExpense) {
        return AddRoutineResponse.builder()
                .routineId(monthlyRoutineExpense.getId())
                .build();
    }

}
