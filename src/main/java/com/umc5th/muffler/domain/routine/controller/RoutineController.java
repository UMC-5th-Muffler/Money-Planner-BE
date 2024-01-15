package com.umc5th.muffler.domain.routine.controller;

import com.umc5th.muffler.domain.routine.dto.*;
import com.umc5th.muffler.domain.routine.service.RoutineCreateService;
import com.umc5th.muffler.domain.routine.service.RoutineService;
import com.umc5th.muffler.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/routine")
public class RoutineController {

    private final RoutineCreateService routineCreateService;
    private final RoutineService routineService;

    @PostMapping("/weekly")
    public Response<Void> addWeeklyRoutine(@RequestBody @Valid AddWeeklyRoutineRequest request) {

        routineCreateService.addWeeklyRoutine(request);
        routineCreateService.addPastExpenses(request);
        return Response.success();
    }

    @PostMapping("/monthly")
    public Response<Void> addMonthlyRoutine(@RequestBody @Valid AddMonthlyRoutineRequest request) {

        routineCreateService.addMonthlyRoutine(request);
        return Response.success();
    }

    @DeleteMapping("/{routineId}")
    public Response<Void> deleteRoutine(@PathVariable Long routineId, @RequestParam Long memberId) {

        routineService.delete(routineId, memberId);
        return Response.success();
    }
}
