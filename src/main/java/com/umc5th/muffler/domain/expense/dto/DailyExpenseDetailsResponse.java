package com.umc5th.muffler.domain.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DailyExpenseDetailsResponse {

    private LocalDate date;
    private Boolean isZeroDay;
    private Long dailyTotalCost;
    private List<CategoryDetailDto> categoryList;
    private List<ExpenseDetailDto> expenseDetailDtoList;
    private boolean hasNext;
}
