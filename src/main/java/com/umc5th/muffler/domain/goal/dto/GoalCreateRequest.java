package com.umc5th.muffler.domain.goal.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoalCreateRequest {
    private String icon;
    private String title;
    private String detail;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long totalBudget;

    private List<CategoryGoalRequest> categoryGoals;

    private List<Long> dailyBudgets;
}
