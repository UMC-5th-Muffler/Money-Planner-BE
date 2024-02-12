package com.umc5th.muffler.domain.goal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GoalReportResponse {
    private long zeroDayCount;
    private List<CategoryTotalCostDto> categoryTotalCosts;
    private List<CategoryGoalReportDto> categoryGoalReports;
}
