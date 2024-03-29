package com.umc5th.muffler.domain.routine.controller;

import com.umc5th.muffler.domain.routine.dto.RoutineDetail;
import com.umc5th.muffler.domain.routine.dto.RoutineResponse;
import com.umc5th.muffler.domain.routine.service.RoutineService;
import com.umc5th.muffler.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/routine")
public class RoutineController {

    private final RoutineService routineService;

    @GetMapping
    public Response<RoutineResponse> getAllRoutines(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam (name = "endPoint", required = false) Long endPointId,
            Authentication authentication) {

        RoutineResponse response = routineService.getAllRoutines(pageable, endPointId, authentication.getName());
        return Response.success(response);
    }

    @GetMapping("/{routineId}")
    public Response<RoutineDetail> getRoutine(@PathVariable Long routineId, Authentication authentication) {

        RoutineDetail response = routineService.getRoutine(authentication.getName(), routineId);
        return Response.success(response);
    }

    @DeleteMapping("/{routineId}")
    public Response<Void> delete(@PathVariable Long routineId, Authentication authentication) {
        routineService.delete(routineId, authentication.getName());
        return Response.success();
    }
}