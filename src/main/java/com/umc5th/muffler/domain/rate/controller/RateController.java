package com.umc5th.muffler.domain.rate.controller;

import com.umc5th.muffler.domain.rate.dto.RateCreateRequest;
import com.umc5th.muffler.domain.rate.dto.RateCriteriaResponse;
import com.umc5th.muffler.domain.rate.dto.RateUpdateRequest;
import com.umc5th.muffler.domain.rate.service.RateService;
import com.umc5th.muffler.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rate")
public class RateController {

    private final RateService rateService;

    @GetMapping
    public Response<RateCriteriaResponse> getRateCriteria(@RequestParam(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                                          Authentication authentication){
        RateCriteriaResponse response = rateService.getRateCriteria(date, authentication.getName());
        return Response.success(response);
    }

    @PostMapping
    public Response<Void> createRate(@RequestBody @Valid RateCreateRequest request, Authentication authentication){
        rateService.createRate(request, authentication.getName());
        return Response.success();
    }

    @PatchMapping
    public Response<Void> updateRate(@RequestBody @Valid RateUpdateRequest request){
        rateService.updateRate(request);
        return Response.success();
    }
}
