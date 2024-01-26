package com.umc5th.muffler.domain.expense.controller;

import com.umc5th.muffler.domain.expense.dto.*;
import com.umc5th.muffler.domain.expense.service.ExpenseViewService;
import com.umc5th.muffler.domain.expense.service.ExpenseService;
import com.umc5th.muffler.global.response.Response;
import com.umc5th.muffler.global.validation.ValidOrder;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expense")
public class ExpenseController {
    private final ExpenseService expenseService;
    private final ExpenseViewService expenseViewService;

    @PostMapping("")
    public Response<NewExpenseResponse> enrollNewExpense(@RequestBody @Valid NewExpenseRequest request) {
        NewExpenseResponse result = expenseService.enrollExpense(request);
        return Response.success(result);
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
            @RequestParam(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @PageableDefault(size = 20) @SortDefault.SortDefaults({
                    @SortDefault(sort = "date", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            }) Pageable pageable){

        WeeklyExpenseResponse response = expenseViewService.getWeeklyExpenseDetails(authentication.getName(),date, pageable);
        return Response.success(response);
    }

    @GetMapping("/monthly")
    public Response<MonthlyExpenseResponse> getHomeExpenses(
            Authentication authentication,
            @RequestParam @Positive int year,
            @RequestParam @Range(min = 1, max = 12) int month,
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
            response = expenseViewService.getHomeExpensesWithCategory(authentication.getName(), year, month, goalId, categoryId, order, pageable);
        } else {
            response = expenseViewService.getHomeMonthlyExpenses(authentication.getName(), year, month, goalId, order, pageable);
        }
        return Response.success(response);
    }

}
