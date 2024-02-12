package com.umc5th.muffler.domain.expense.controller;

import com.umc5th.muffler.domain.expense.dto.*;
import com.umc5th.muffler.domain.expense.service.ExpenseSearchService;
import com.umc5th.muffler.domain.expense.service.ExpenseService;
import com.umc5th.muffler.domain.expense.service.ExpenseViewService;
import com.umc5th.muffler.global.response.Response;
import com.umc5th.muffler.global.validation.ValidOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.security.Principal;
import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/expense")
public class ExpenseController {
    private final ExpenseSearchService expenseSearchService;
    private final ExpenseService expenseService;
    private final ExpenseViewService expenseViewService;

    @PostMapping
    public Response<ExpenseResponse> create(Principal principal,
                                            @RequestBody @Valid ExpenseCreateRequest request) {
        return Response.success(expenseService.create(principal.getName(), request));
    }

    @PatchMapping
    public Response<ExpenseResponse> update(Principal principal,
                                            @RequestBody @Valid ExpenseUpdateRequest request) {
        return Response.success(expenseService.update(principal.getName(), request));
    }

    @DeleteMapping("/{expenseId}")
    public Response<Void> delete(Principal principal, @PathVariable("expenseId") Long expenseId) {
        expenseService.delete(principal.getName(), expenseId);
        return Response.success();
    }

    @GetMapping("/{expenseId}")
    public Response<ExpenseDto> getExpense(Authentication authentication, @PathVariable Long expenseId) {
        ExpenseDto response = expenseViewService.getExpense(authentication.getName(), expenseId);
        return Response.success(response);
    }

    @GetMapping("/daily")
    public Response<DailyExpenseResponse> getDailyExpenseDetails(
            Authentication authentication,
            @RequestParam(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(name = "lastExpenseId", required = false) Long lastExpenseId,
            @PageableDefault(size = 20) Pageable pageable){

        DailyExpenseResponse response = expenseViewService.getDailyExpenseDetails(authentication.getName(), date, lastExpenseId, pageable);
        return Response.success(response);
    }

    @GetMapping("/weekly")
    public Response<WeeklyExpenseResponse> getWeeklyExpenseDetails(
            Authentication authentication,
            @RequestParam(name = "goalId") Long goalId,
            @RequestParam(name = "startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(name = "endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(name = "lastDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate lastDate,
            @RequestParam(name = "lastExpenseId", required = false) Long lastExpenseId,
            @RequestParam(name = "size", defaultValue = "20") @Positive int size) {

        WeeklyExpenseResponse response = expenseViewService.getWeeklyExpenseDetails(authentication.getName(), goalId, startDate, endDate, lastDate, lastExpenseId, size);
        return Response.success(response);
    }

    @GetMapping("/monthly")
    public Response<MonthlyExpenseResponse> getHomeExpenses(
            Authentication authentication,
            @RequestParam(name = "yearMonth") @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth,
            @RequestParam(name = "goalId", required = false) Long goalId,
            @RequestParam(name = "order", defaultValue = "DESC") @ValidOrder String order,
            @RequestParam(name = "lastDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate lastDate,
            @RequestParam(name = "lastExpenseId", required = false) Long lastExpenseId,
            @RequestParam(name = "size", defaultValue = "20") @Positive int size,
            @RequestParam(name = "categoryId", required = false) Long categoryId) {

        MonthlyExpenseResponse response;
        if (categoryId != null) {
            response = expenseViewService.getMonthlyExpensesWithCategory(authentication.getName(), yearMonth, goalId, categoryId, order, lastDate, lastExpenseId, size);
        } else {
            response = expenseViewService.getMonthlyExpenses(authentication.getName(), yearMonth, goalId, order, lastDate, lastExpenseId, size);
        }
        return Response.success(response);
    }

    @GetMapping("/search")
    public Response<SearchResponse> getSearchExpense(
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "size", defaultValue = "10") @Positive int size,
            @RequestParam(name = "order", defaultValue = "DESC") @ValidOrder String order,
            @RequestParam(name = "lastDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate lastDate,
            @RequestParam(name = "lastExpenseId", required = false) Long lastExpenseId,
            Authentication authentication) {

        SearchResponse response = expenseSearchService.searchExpense(authentication.getName(), title, size, order, lastDate, lastExpenseId);
        return Response.success(response);
    }
}
