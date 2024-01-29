package com.umc5th.muffler.domain.expense.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.umc5th.muffler.entity.constant.Rate;
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Rate rate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String rateMemo;
    private List<ExpenseDetailDto> expenseDetailList;
    private boolean hasNext;
}
