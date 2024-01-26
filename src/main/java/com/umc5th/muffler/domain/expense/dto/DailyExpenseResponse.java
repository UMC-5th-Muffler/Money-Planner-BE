package com.umc5th.muffler.domain.expense.dto;

import com.umc5th.muffler.entity.constant.Level;
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
public class DailyExpenseResponse {

    private LocalDate date;
    private Boolean isZeroDay;
    private Long dailyTotalCost;
    private Level rateLevel;
    private String rateMemo;
    private List<ExpenseDetailDto> expenseDetailList;
    private boolean hasNext;
}
