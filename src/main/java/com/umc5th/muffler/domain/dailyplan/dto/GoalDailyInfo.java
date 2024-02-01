package com.umc5th.muffler.domain.dailyplan.dto;

import com.umc5th.muffler.entity.constant.Rate;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GoalDailyInfo {
    private Long dailyBudget;
    private Long dailyTotalCost;
    private Rate dailyRate;
    private Boolean isZeroDay;
}
