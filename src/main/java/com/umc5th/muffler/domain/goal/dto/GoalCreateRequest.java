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
    private LocalDate startDate;
    private LocalDate endDate;
    private String title;
    private String detail;
    private String icon;
    private Long totalBudget;
    private List<Long> dailyBudgets;
}
