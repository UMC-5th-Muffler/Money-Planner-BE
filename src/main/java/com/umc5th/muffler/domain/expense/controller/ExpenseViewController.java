package com.umc5th.muffler.domain.expense.controller;

import com.umc5th.muffler.domain.expense.dto.DailyExpenseResponse;
import com.umc5th.muffler.domain.expense.dto.ExpenseDto;
import com.umc5th.muffler.domain.expense.dto.MonthlyExpenseResponse;
import com.umc5th.muffler.domain.expense.dto.WeeklyExpenseResponse;
import com.umc5th.muffler.domain.expense.service.ExpenseViewService;
import com.umc5th.muffler.global.response.Response;
import com.umc5th.muffler.global.validation.ValidOrder;
import java.time.LocalDate;
import java.time.YearMonth;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/expense")
public class ExpenseViewController {
    private final ExpenseViewService expenseViewService;

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
}
