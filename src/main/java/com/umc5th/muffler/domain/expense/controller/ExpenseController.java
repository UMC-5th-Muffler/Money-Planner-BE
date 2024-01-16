package com.umc5th.muffler.domain.expense.controller;

import com.umc5th.muffler.domain.expense.dto.NewExpenseRequest;
import com.umc5th.muffler.domain.expense.dto.NewExpenseResponse;
import com.umc5th.muffler.domain.expense.service.ExpenseService;
import com.umc5th.muffler.global.response.Response;
import javax.validation.Valid;
import com.umc5th.muffler.domain.expense.dto.DailyExpenseDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
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
            @PageableDefault(size = 20, sort = "createdAt",  direction = Sort.Direction.ASC) Pageable pageable){

        DailyExpenseDetailsResponse response = expenseService.getDailyExpenseDetails(date, pageable);
        return Response.success(response);
    }

}
