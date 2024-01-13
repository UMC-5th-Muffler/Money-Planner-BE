package com.umc5th.muffler.domain.routine.converter;

import com.umc5th.muffler.domain.routine.dto.WeeklyRoutineRequest;
import com.umc5th.muffler.domain.routine.dto.WeeklyRoutineResponse;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.WeeklyRoutineExpense;
import com.umc5th.muffler.entity.WeeklyRoutineExpenseDetail;

import java.time.DayOfWeek;

public class RoutineConverter {

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


    public static WeeklyRoutineResponse toAddWeeklyRoutineResult(WeeklyRoutineExpense weeklyRoutineExpense) {
        return WeeklyRoutineResponse.builder()
                .weeklyRoutineId(weeklyRoutineExpense.getId())
                .build();
    }

}
