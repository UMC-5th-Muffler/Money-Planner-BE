package com.umc5th.muffler.domain.rate.controller;

import com.umc5th.muffler.domain.rate.dto.RateCriteriaResponse;
import com.umc5th.muffler.domain.rate.service.RateService;
import com.umc5th.muffler.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rate")
public class RateController {

    private final RateService rateService;

    @GetMapping("")
    public Response<RateCriteriaResponse> getRateCriteria(@RequestParam(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date){
        RateCriteriaResponse response = rateService.getEvalCategoryList(date);
        return Response.success(response);
    }
}
