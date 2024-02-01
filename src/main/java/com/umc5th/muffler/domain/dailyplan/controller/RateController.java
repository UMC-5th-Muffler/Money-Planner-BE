package com.umc5th.muffler.domain.dailyplan.controller;

import com.umc5th.muffler.domain.dailyplan.dto.RateInfoResponse;
import com.umc5th.muffler.domain.dailyplan.dto.RateUpdateRequest;
import com.umc5th.muffler.domain.dailyplan.service.RateService;
import com.umc5th.muffler.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rate")
public class RateController {

    private final RateService rateService;

    @GetMapping
    public Response<RateInfoResponse> getRateInfo(Authentication authentication, @RequestParam(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date){
        RateInfoResponse response = rateService.getRateInfo(authentication.getName(), date);
        return Response.success(response);
    }

    @PatchMapping("/{date}")
    public Response<Void> updateRate(Authentication authentication,
                                     @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                     @RequestBody @Valid RateUpdateRequest request){
        rateService.updateRate(authentication.getName(), date, request);
        return Response.success();
    }
}
