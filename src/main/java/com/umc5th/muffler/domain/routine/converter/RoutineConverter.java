package com.umc5th.muffler.domain.routine.converter;

import com.umc5th.muffler.domain.routine.dto.MonthlyRoutineRequest;
import com.umc5th.muffler.domain.routine.dto.MonthlyRoutineResponse;
import com.umc5th.muffler.domain.routine.dto.WeeklyRoutineRequest;
import com.umc5th.muffler.domain.routine.dto.WeeklyRoutineResponse;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.MonthlyRoutineExpense;
import com.umc5th.muffler.entity.WeeklyRoutineExpense;
import com.umc5th.muffler.entity.WeeklyRoutineExpenseDetail;

import java.time.DayOfWeek;

public class RoutineConverter {

    // WeeklyRoutineRequest(dto) -> WeeklyRoutineExpense(entity)
    public static WeeklyRoutineExpense toWeeklyRoutine(WeeklyRoutineRequest request, Member member) {
        WeeklyRoutineExpense weeklyRoutineExpense = WeeklyRoutineExpense.builder()
                .member(member)
                .cost(request.getCost())
                .term(request.getTerm())
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

    // WeeklyRoutineExpense(entity) -> WeeklyRoutineResponse(dto)
    public static WeeklyRoutineResponse toAddWeeklyRoutineResult(WeeklyRoutineExpense weeklyRoutineExpense) {
        return WeeklyRoutineResponse.builder()
                .weeklyRoutineId(weeklyRoutineExpense.getId())
                .build();
    }

    // MonthlyRoutineRequest(dto) -> MonthlyRoutineExpense(entity)
    public static MonthlyRoutineExpense toMonthlyRoutine(MonthlyRoutineRequest request, Member member) {
        MonthlyRoutineExpense monthlyRoutineExpense = MonthlyRoutineExpense.builder()
                .member(member)
                .cost(request.getCost())
                .day(request.getDay())
                .endDate(request.getEndDate())
                .build();

        return monthlyRoutineExpense;
    }

    // MonthlyRoutineExpense(entity) -> MonthlyRoutineResponse(dto)
    public static MonthlyRoutineResponse toAddMonthlyRoutineResult(MonthlyRoutineExpense monthlyRoutineExpense) {
        return MonthlyRoutineResponse.builder()
                .monthlyRoutineId(monthlyRoutineExpense.getId())
                .build();
    }

}
