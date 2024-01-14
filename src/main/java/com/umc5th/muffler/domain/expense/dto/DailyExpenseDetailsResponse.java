package com.umc5th.muffler.domain.expense.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
public class DailyExpenseDetailsResponse {

    private LocalDate date;
    private Long dailyTotalCost;
    private List<CategoryDetailDto> categoryList;
    private List<ExpenseDetailDto> expenseDetailDtoList;
    private boolean hasNext;
}
