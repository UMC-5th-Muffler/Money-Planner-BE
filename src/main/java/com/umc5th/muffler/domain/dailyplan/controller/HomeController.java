package com.umc5th.muffler.domain.dailyplan.controller;

import com.umc5th.muffler.domain.dailyplan.dto.CategoryCalendar;
import com.umc5th.muffler.domain.dailyplan.dto.WholeCalendar;
import com.umc5th.muffler.domain.dailyplan.service.HomeService;
import com.umc5th.muffler.global.response.Response;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/basic")
    public Response<WholeCalendar> getBasicCalendar(@RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth, Authentication authentication) {
        return Response.success(homeService.getBasicCalendar(authentication.getName(), yearMonth));
    }

