package com.umc5th.muffler.domain.routine.controller;

import com.umc5th.muffler.domain.routine.converter.RoutineConverter;
import com.umc5th.muffler.domain.routine.dto.MonthlyRoutineRequest;
import com.umc5th.muffler.domain.routine.dto.MonthlyRoutineResponse;
import com.umc5th.muffler.domain.routine.dto.WeeklyRoutineRequest;
import com.umc5th.muffler.domain.routine.dto.WeeklyRoutineResponse;
import com.umc5th.muffler.domain.routine.service.RoutineService;
import com.umc5th.muffler.entity.MonthlyRoutineExpense;
import com.umc5th.muffler.entity.WeeklyRoutineExpense;
import com.umc5th.muffler.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/routine")
public class RoutineController {

    private final RoutineService routineService;

    @PostMapping("/weekly")
    public Response<WeeklyRoutineResponse> addWeeklyRoutine(@RequestBody WeeklyRoutineRequest request) {

        WeeklyRoutineExpense weeklyRoutineExpense = routineService.addWeeklyRoutine(request);
        WeeklyRoutineResponse weeklyRoutineResponse = RoutineConverter.toAddWeeklyRoutineResult(weeklyRoutineExpense);

        return Response.success(weeklyRoutineResponse);
    }

    @PostMapping("/monthly")
    public Response<MonthlyRoutineResponse> addMonthlyRoutine(@RequestBody MonthlyRoutineRequest request) {

        MonthlyRoutineExpense monthlyRoutineExpense = routineService.addMonthlyRoutine(request);
        MonthlyRoutineResponse monthlyRoutineResponse = RoutineConverter.toAddMonthlyRoutineResult(monthlyRoutineExpense);

        return Response.success(monthlyRoutineResponse);
    }
}
