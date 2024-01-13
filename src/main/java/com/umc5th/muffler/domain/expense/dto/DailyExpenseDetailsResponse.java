package com.umc5th.muffler.domain.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DailyExpenseDetailsResponse {

    private LocalDate date;
    private Long dailyTotalCost;
    private List<ExpenseDetail> expenseDetailList;
}
