package com.umc5th.muffler.domain.expense.controller;

import com.umc5th.muffler.domain.expense.dto.homeDto.WholeCalendarResponse;
import com.umc5th.muffler.domain.expense.service.HomeService;
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
    public Response<WholeCalendarResponse> getCalendar(
            @RequestParam(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(name = "year") Integer year,
            @RequestParam(name = "month") Integer month)
    {
        WholeCalendarResponse response = homeService.getWholeCalendarInfos(date, year, month);
        return Response.success(response);
    }

    @GetMapping("/{goalId}")
    public Response<WholeCalendarResponse> getGoalCalendar(@PathVariable Long goalId) {
        WholeCalendarResponse response = homeService.getGoalCalendarInfos(goalId);
        return Response.success(response);
    }
}
