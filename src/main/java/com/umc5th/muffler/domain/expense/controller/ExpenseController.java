package com.umc5th.muffler.domain.expense.controller;

import com.umc5th.muffler.domain.expense.dto.NewExpenseRequest;
import com.umc5th.muffler.domain.expense.dto.NewExpenseResponse;
import com.umc5th.muffler.domain.expense.service.ExpenseService;
import com.umc5th.muffler.global.response.Response;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expense")
public class ExpenseController {
    private final ExpenseService expenseService;

    public Response<NewExpenseResponse> enrollNewExpense(@RequestBody @Valid NewExpenseRequest request) {
        return Response.success(new NewExpenseResponse(0L));
    }
}
