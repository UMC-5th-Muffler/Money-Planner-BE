package com.umc5th.muffler.domain.expense.controller;

import com.umc5th.muffler.domain.expense.dto.ExpenseCreateRequest;
import com.umc5th.muffler.domain.expense.dto.ExpenseOverview;
import com.umc5th.muffler.domain.expense.dto.ExpenseResponse;
import com.umc5th.muffler.domain.expense.dto.ExpenseUpdateRequest;
import com.umc5th.muffler.domain.expense.dto.SearchResponse;
import com.umc5th.muffler.domain.expense.service.ExpenseSearchService;
import com.umc5th.muffler.domain.expense.service.ExpenseService;
import com.umc5th.muffler.global.response.Response;
import com.umc5th.muffler.global.validation.ValidOrder;
import java.security.Principal;
import java.time.LocalDate;
import java.time.YearMonth;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/expense")
public class ExpenseController {
    private final ExpenseService expenseService;
    private final ExpenseSearchService expenseSearchService;

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

    @GetMapping("/overview/{yearMonth}")
    public Response<ExpenseOverview> getExpenseOverview(Authentication authentication,
                                                        @PathVariable @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {
        return Response.success(expenseService.getOverview(authentication.getName(), yearMonth));
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
