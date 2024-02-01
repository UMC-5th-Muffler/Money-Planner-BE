package com.umc5th.muffler.domain.goal.controller;

import com.umc5th.muffler.domain.goal.dto.ToggleZeroDayRequest;
import com.umc5th.muffler.domain.goal.service.DailyPlanService;

import com.umc5th.muffler.global.response.Response;
import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/dailyPlan")
@RequiredArgsConstructor
public class DailyPlanController {
    private final DailyPlanService dailyPlanService;

    @PatchMapping("/zeroDay")
    public Response<Void> toggleZeroDay(Principal principal, @RequestBody @Valid ToggleZeroDayRequest request) {
        dailyPlanService.updateZeroDay(principal.getName(), request.getDailyPlanDate());
        return Response.success();
    }
}
