package com.umc5th.muffler.domain.dailyplan.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCalendar {
    private LocalDate startDate;
    private LocalDate endDate;
    private Long categoryId;
    private String categoryName;
    private Long categoryTotalCost;
    private List<Long> categoryDailyCost;

    // CategoryGoal 있는 경우만
    private Long categoryBudget;
}
