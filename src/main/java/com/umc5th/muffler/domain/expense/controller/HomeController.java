package com.umc5th.muffler.domain.expense.controller;

import com.umc5th.muffler.domain.expense.dto.homeDto.OtherGoalsInfo;
import com.umc5th.muffler.domain.expense.dto.homeDto.OtherGoalsResponse;
import com.umc5th.muffler.domain.expense.dto.homeDto.WholeCalendarResponse;
import com.umc5th.muffler.domain.expense.service.HomeService;
import com.umc5th.muffler.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


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

    @GetMapping("/{year}/{month}")
    public Response<OtherGoalsResponse> turnPage(@PathVariable Integer year, @PathVariable Integer month, Authentication authentication) {
        OtherGoalsResponse response = homeService.getTurnPage(authentication.getName(), year, month);
        return Response.success(response);
    }

    @GetMapping("/{goalId}")
    public Response<WholeCalendarResponse> getGoalCalendar(@PathVariable Long goalId, Authentication authentication) {
        WholeCalendarResponse response = homeService.getGoalCalendarInfos(authentication.getName(), goalId);
        return Response.success(response);
    }

    @GetMapping("/{goalId}/{year}/{month}")
    public Response<WholeCalendarResponse> turnPageOfCalendar(
            @PathVariable Long goalId, @PathVariable Integer year,
            @PathVariable Integer month, Authentication authentication) {
        WholeCalendarResponse response = homeService.getGoalTurnPage(authentication.getName(), goalId, year, month);
        return Response.success(response);
    }
}
