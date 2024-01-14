package com.umc5th.muffler.domain.routine.controller;

import com.umc5th.muffler.domain.routine.converter.RoutineConverter;
import com.umc5th.muffler.domain.routine.dto.*;
import com.umc5th.muffler.domain.routine.service.RoutineService;
import com.umc5th.muffler.entity.RoutineExpense;
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

        RoutineExpense routineExpense = routineService.addWeeklyRoutine(request);
        routineService.addPastExpenses(request);
        AddRoutineResponse weeklyAddRoutineResponse = RoutineConverter.toAddRoutineResult(routineExpense);

        return Response.success(weeklyAddRoutineResponse);
    }

    @PostMapping("/monthly")
    public Response<AddRoutineResponse> addMonthlyRoutine(@RequestBody AddMonthlyRoutineRequest request) {

        RoutineExpense routineExpense = routineService.addMonthlyRoutine(request);
        AddRoutineResponse monthlyAddRoutineResponse = RoutineConverter.toAddRoutineResult(routineExpense);

        return Response.success(monthlyAddRoutineResponse);
    }
}
