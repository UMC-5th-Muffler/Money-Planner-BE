package com.umc5th.muffler.domain.dailyplan.dto;

import com.umc5th.muffler.entity.DailyPlan;

public class DailyPlanConverter {

    public static RateInfoResponse toRateInfoResponse(DailyPlan dailyPlan) {

        return RateInfoResponse.builder()
                .dailyPlanBudget(dailyPlan.getBudget())
                .dailyTotalCost(dailyPlan.getTotalCost())
                .rate(dailyPlan.getRate())
                .memo(dailyPlan.getRateMemo())
                .isZeroDay(dailyPlan.getIsZeroDay())
                .build();
    }
}
