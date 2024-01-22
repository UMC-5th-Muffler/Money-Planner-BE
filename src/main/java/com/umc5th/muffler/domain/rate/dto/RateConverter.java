package com.umc5th.muffler.domain.rate.dto;

import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Rate;
import com.umc5th.muffler.entity.constant.Level;

public class RateConverter {

    public static RateCriteriaResponse toRateCriteriaResponse(DailyPlan dailyPlan, Rate rate) {
        Long rateId = (rate != null) ? rate.getId() : null;
        String memo = (rate != null) ? rate.getMemo() : null;
        Level totalLevel = (rate != null) ? rate.getTotalLevel() : null;

        return RateCriteriaResponse.builder()
                .dailyPlanBudget(dailyPlan.getBudget())
                .dailyTotalCost(dailyPlan.getTotalCost())
                .rateId(rateId)
                .memo(memo)
                .isZeroDay(dailyPlan.getIsZeroDay())
                .totalLevel(totalLevel)
                .build();
    }

    public static Rate toRate(RateCreateRequest request){
        return Rate.builder()
                .totalLevel(Level.valueOf(request.getTotalLevel()))
                .memo(request.getMemo())
                .build();
    }

}
