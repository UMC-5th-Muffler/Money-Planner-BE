package com.umc5th.muffler.domain.routine.controller;

import com.umc5th.muffler.domain.routine.converter.RoutineConverter;
import com.umc5th.muffler.domain.routine.dto.*;
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
    public Response<AddRoutineResponse> addWeeklyRoutine(@RequestBody AddWeeklyRoutineRequest request) {

        WeeklyRoutineExpense weeklyRoutineExpense = routineService.addWeeklyRoutine(request);
        AddRoutineResponse weeklyAddRoutineResponse = RoutineConverter.toAddWeeklyRoutineResult(weeklyRoutineExpense);

        return Response.success(weeklyAddRoutineResponse);
    }

    @PostMapping("/monthly")
    public Response<AddRoutineResponse> addMonthlyRoutine(@RequestBody AddMonthlyRoutineRequest request) {

        MonthlyRoutineExpense monthlyRoutineExpense = routineService.addMonthlyRoutine(request);
        AddRoutineResponse monthlyAddRoutineResponse = RoutineConverter.toAddMonthlyRoutineResult(monthlyRoutineExpense);

        return Response.success(monthlyAddRoutineResponse);
    }
}
