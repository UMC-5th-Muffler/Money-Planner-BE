package com.umc5th.muffler.domain.dailyplan.controller;

import com.umc5th.muffler.domain.dailyplan.dto.RateInfoResponse;
import com.umc5th.muffler.domain.dailyplan.dto.RateUpdateRequest;
import com.umc5th.muffler.domain.dailyplan.service.DailyPlanService;
import com.umc5th.muffler.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dailyplan")
public class DailyPlanController {

    private final DailyPlanService dailyPlanService;

    @GetMapping("/rate")
    public Response<RateInfoResponse> getRateInfo(@RequestParam(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date){
        RateInfoResponse response = dailyPlanService.getRateInfo(date);
        return Response.success(response);
    }

    @PatchMapping("/rate/{date}")
    public Response<Void> updateRate(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, @RequestBody @Valid RateUpdateRequest request){
        dailyPlanService.updateRate(date, request);
        return Response.success();
    }
}
