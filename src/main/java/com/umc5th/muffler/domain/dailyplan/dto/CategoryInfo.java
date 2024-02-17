package com.umc5th.muffler.domain.dailyplan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryInfo implements CalendarInfo {
    private Long categoryId;
    private String name;
    private Long categoryTotalCost;

    // CategoryGoal 있는 경우만
    private Long categoryBudget;
}
