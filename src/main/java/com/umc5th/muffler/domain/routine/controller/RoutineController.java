package com.umc5th.muffler.domain.routine.controller;

import com.umc5th.muffler.domain.routine.dto.RoutineResponse;
import com.umc5th.muffler.domain.routine.service.RoutineService;
import com.umc5th.muffler.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/routine")
public class RoutineController {

    private final RoutineService routineService;

    @GetMapping
    public Response<RoutineResponse> getRoutine(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        RoutineResponse response = routineService.getRoutine(pageable);
        return Response.success(response);
    }

    @DeleteMapping("/{routineId}")
    public Response<Void> delete(@PathVariable Long routineId) {
        routineService.delete(routineId);
        return Response.success();
    }
}