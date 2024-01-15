package com.umc5th.muffler.domain.routine.controller;

import com.umc5th.muffler.domain.routine.dto.*;
import com.umc5th.muffler.domain.routine.service.RoutineService;
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
    public Response<Void> addWeeklyRoutine(@RequestBody AddWeeklyRoutineRequest request) { // TODO: @Valid 추가

        routineService.addWeeklyRoutine(request);
        routineService.addPastExpenses(request);
        return Response.success();
    }

    @PostMapping("/monthly")
    public Response<Void> addMonthlyRoutine(@RequestBody AddMonthlyRoutineRequest request) { // TODO: @Valid 추가

        routineService.addMonthlyRoutine(request);
        return Response.success();
    }
}
