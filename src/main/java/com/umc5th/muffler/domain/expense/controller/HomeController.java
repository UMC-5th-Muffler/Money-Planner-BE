package com.umc5th.muffler.domain.expense.controller;

import com.umc5th.muffler.domain.expense.dto.homeDto.WholeCalendarResponse;
import com.umc5th.muffler.domain.expense.service.HomeService;
import com.umc5th.muffler.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeController {

    private final HomeService homeService;

    @GetMapping
    public Response<WholeCalendarResponse> getCalendar(Authentication authentication) {
        WholeCalendarResponse response = homeService.getWholeCalendarInfos(authentication.getName());
        return Response.success(response);
    }

    @GetMapping("/{goalId}")
    public Response<WholeCalendarResponse> getGoalCalendar(@PathVariable Long goalId, Authentication authentication) {
        WholeCalendarResponse response = homeService.getGoalCalendarInfos(goalId, authentication.getName());
        return Response.success(response);
    }
}
