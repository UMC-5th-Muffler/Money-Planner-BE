package com.umc5th.muffler.domain.expense.controller;

import com.umc5th.muffler.domain.expense.converter.ExpenseConverter;
import com.umc5th.muffler.domain.expense.dto.DailyExpenseDetailsResponse;
import com.umc5th.muffler.domain.expense.service.ExpenseService;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expense")
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping("/daily")
    public Response<DailyExpenseDetailsResponse> getDailyExpenseDetails(
            @RequestParam(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(name = "page") Integer page){

        DailyExpenseDetailsResponse response = expenseService.getDailyExpenseDetails(date, page);
        return Response.success(response);
    }

}
