package com.umc5th.muffler.domain.expense.controller;

import com.umc5th.muffler.domain.expense.dto.*;
import com.umc5th.muffler.domain.expense.service.ExpenseService;
import com.umc5th.muffler.domain.expense.service.ExpenseViewService;
import com.umc5th.muffler.global.response.Response;
import com.umc5th.muffler.global.validation.ValidOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/expense")
public class ExpenseController {
    private final ExpenseService expenseService;
    private final ExpenseViewService expenseViewService;

    @PostMapping("")
    public Response<NewExpenseResponse> enrollNewExpense(@RequestBody @Valid NewExpenseRequest request) {
        NewExpenseResponse result = expenseService.enrollExpense(request);
        return Response.success(result);
    }

    @GetMapping("/{id}")
    public Response<ExpenseDto> getExpense(Authentication authentication, @PathVariable Long id){
        ExpenseDto response = expenseViewService.getExpense(authentication.getName(), id);
        return Response.success(response);
    }

    @GetMapping("/daily")
    public Response<DailyExpenseResponse> getDailyExpenseDetails(
            Authentication authentication,
            @RequestParam(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @PageableDefault(size = 20, sort = "createdAt",  direction = Sort.Direction.DESC) Pageable pageable){

        DailyExpenseResponse response = expenseViewService.getDailyExpenseDetails(authentication.getName(), date, pageable);
        return Response.success(response);
    }

    @GetMapping("/weekly")
    public Response<WeeklyExpenseResponse> getWeeklyExpenseDetails(
            Authentication authentication,
            @RequestParam(name = "goalId") Long goalId,
            @Valid WeekRequestParam weekRequestParam,
            @PageableDefault(size = 20) @SortDefault.SortDefaults({
                    @SortDefault(sort = "date", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            }) Pageable pageable){

        WeeklyExpenseResponse response = expenseViewService.getWeeklyExpenseDetails(authentication.getName(), goalId, weekRequestParam.getStartDate(), weekRequestParam.getEndDate(), pageable);
        return Response.success(response);
    }

    @GetMapping("/monthly")
    public Response<MonthlyExpenseResponse> getHomeExpenses(
            Authentication authentication,
            @RequestParam(name = "yearMonth") @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth,
            @RequestParam(name = "goalId", required = false) Long goalId,
            @RequestParam(name = "order", defaultValue = "DESC") @ValidOrder String order,
            @RequestParam(name = "page", defaultValue = "0") @Min(value = 0) int page,
            @RequestParam(name = "size", defaultValue = "20") @Positive int size,
            @RequestParam(name = "categoryId", required = false) Long categoryId)
    {
        Sort.Direction direction = order.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "date").and(Sort.by(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page, size, sort);

        MonthlyExpenseResponse response;
        if (categoryId != null) {
            response = expenseViewService.getMonthlyExpensesWithCategory(authentication.getName(), yearMonth, goalId, categoryId, order, pageable);
        } else {
            response = expenseViewService.getMonthlyExpenses(authentication.getName(), yearMonth, goalId, order, pageable);
        }
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
