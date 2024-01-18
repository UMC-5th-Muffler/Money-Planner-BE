package com.umc5th.muffler.domain.home.controller;

import com.umc5th.muffler.domain.home.dto.CategoryGoalCalendarResponse;
import com.umc5th.muffler.domain.home.dto.CategoryNoGoalCalendarResponse;
import com.umc5th.muffler.domain.home.dto.WholeCalendarResponse;
import com.umc5th.muffler.domain.home.service.HomeService;
import com.umc5th.muffler.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeController {

    private final HomeService homeService;

    @GetMapping
    public Response<WholeCalendarResponse> getCalendar(@RequestParam(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

        WholeCalendarResponse response = homeService.getWholeCalendarInfos(date);
        return Response.success(response);
    }

    @GetMapping("/{goalId}/category")
    public Response<CategoryGoalCalendarResponse> getCategoryGoalCalendar(
            @PathVariable Long goalId,
            @RequestParam(name = "categoryGoalId") Long categoryGoalId) {

        CategoryGoalCalendarResponse response = homeService.getCategoryGoalInfos(goalId, categoryGoalId);
        return Response.success(response);
    }

    @GetMapping("/{goalId}")
    public Response<CategoryNoGoalCalendarResponse> getCategoryNoGoalCalendar(
            @PathVariable Long goalId,
            @RequestParam(name = "categoryId") Long categoryId) {

        CategoryNoGoalCalendarResponse response = homeService.getCategoryNoGoalInfos(goalId, categoryId);
        return Response.success(response);
    }
}
