package com.umc5th.muffler.domain.home.controller;

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
    public Response<WholeCalendarResponse> getCalendar(
            @RequestParam(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(name = "month") Integer month)
    {
        WholeCalendarResponse response = homeService.getWholeCalendarInfos(date, month);
        return Response.success(response);
    }
}
