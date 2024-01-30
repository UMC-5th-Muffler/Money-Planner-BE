package com.umc5th.muffler.domain.dailyplan.dto;

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
public class ActiveGoalResponse {
    private Long goalId;
    private String goalTitle;
    private Long goalBudget;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long totalCost;
    private List<GoalDailyInfo> dailyList;
}
