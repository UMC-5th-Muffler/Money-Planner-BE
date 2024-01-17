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
public class WeeklyExpenseDetailsResponse {

    private LocalDate startDate;
    private LocalDate endDate;
    private Long weeklyTotalCost;
    private List<CategoryDetailDto> categoryList;
    private List<DailyExpenseDetailsDto> dailyExpenseList;
    private boolean hasNext;
}
