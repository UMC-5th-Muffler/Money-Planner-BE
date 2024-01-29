package com.umc5th.muffler.domain.expense.controller;

import com.umc5th.muffler.domain.expense.dto.*;
import com.umc5th.muffler.domain.expense.service.ExpenseService;
import com.umc5th.muffler.global.response.Response;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

import com.umc5th.muffler.domain.expense.dto.DailyExpenseDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/expense")
public class ExpenseController {
    private final ExpenseService expenseService;

    @PostMapping("")
    public Response<NewExpenseResponse> enrollNewExpense(@RequestBody @Valid NewExpenseRequest request) {
        NewExpenseResponse result = expenseService.enrollExpense(request);
        return Response.success(result);
    }

    @GetMapping("/daily")
    public Response<DailyExpenseDetailsResponse> getDailyExpenseDetails(
            @RequestParam(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @PageableDefault(size = 20, sort = "createdAt",  direction = Sort.Direction.DESC) Pageable pageable){

        DailyExpenseDetailsResponse response = expenseService.getDailyExpenseDetails(date, pageable);
        return Response.success(response);
    }

    @GetMapping("/weekly")
    public Response<WeeklyExpenseDetailsResponse> getWeeklyExpenseDetails(
            @RequestParam(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @PageableDefault(size = 20) @SortDefault.SortDefaults({
                    @SortDefault(sort = "date", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            }) Pageable pageable){

        WeeklyExpenseDetailsResponse response = expenseService.getWeeklyExpenseDetails(date, pageable);
        return Response.success(response);
    }

    @GetMapping("/search")
    public Response<SearchResponse> getSearchExpense(
            @RequestParam(name = "title") String title,
            @RequestParam(name = "page", defaultValue = "0") @Min(value = 0) int page,
            @RequestParam(name = "size", defaultValue = "10") @Positive int size,
            @RequestParam(name = "sort", defaultValue = "DESC") String sortDirection,
            Authentication authentication) {

        SearchResponse response = expenseService.searchExpense(authentication.getName(), title, page, size, sortDirection);
        return Response.success(response);
    }
}
