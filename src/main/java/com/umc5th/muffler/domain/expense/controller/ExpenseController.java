package com.umc5th.muffler.domain.expense.controller;

import com.umc5th.muffler.domain.expense.dto.DailyExpenseDetailsResponse;
import com.umc5th.muffler.domain.expense.dto.WeeklyExpenseDetailsResponse;
import com.umc5th.muffler.domain.expense.service.ExpenseService;
import com.umc5th.muffler.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expense")
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping("/daily")
    public Response<DailyExpenseDetailsResponse> getDailyExpenseDetails(
            @RequestParam(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @PageableDefault(size = 20, sort = "createdAt",  direction = Sort.Direction.ASC) Pageable pageable){

        DailyExpenseDetailsResponse response = expenseService.getDailyExpenseDetails(date, pageable);
        return Response.success(response);
    }

    @GetMapping("/weekly")
    public Response<WeeklyExpenseDetailsResponse> getWeeklyExpenseDetails(
            @RequestParam(name = "startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(name = "endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @PageableDefault(size = 20, sort = "createdAt",  direction = Sort.Direction.ASC) Pageable pageable){

        WeeklyExpenseDetailsResponse response = expenseService.getWeeklyExpenseDetails(startDate, endDate, pageable);
        return Response.success(response);
    }

}
