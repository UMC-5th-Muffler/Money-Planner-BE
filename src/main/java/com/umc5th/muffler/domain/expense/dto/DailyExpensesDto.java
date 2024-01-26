package com.umc5th.muffler.domain.expense.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class DailyExpensesDto {

    private LocalDate date;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long dailyTotalCost;
    private List<ExpenseDetailDto> expenseDetailList;
}
