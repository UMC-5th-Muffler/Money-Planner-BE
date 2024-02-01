package com.umc5th.muffler.domain.routine.controller;

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
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {

        RoutineResponse response = routineService.getAllRoutines(pageable, authentication.getName());
        return Response.success(response);
    }

    @DeleteMapping("/{routineId}")
    public Response<Void> delete(@PathVariable Long routineId, Authentication authentication) {
        routineService.delete(routineId, authentication.getName());
        return Response.success();
    }
}