package com.umc5th.muffler.domain.dailyplan.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoalInfo implements CalendarInfo {
    private Long goalId;
    private String goalTitle;
    private String icon;
    private Long goalBudget;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long totalCost;
}
