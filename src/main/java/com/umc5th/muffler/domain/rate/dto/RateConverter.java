package com.umc5th.muffler.domain.rate.dto;

import com.umc5th.muffler.entity.CategoryGoal;
import com.umc5th.muffler.entity.CategoryRate;
import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Rate;
import com.umc5th.muffler.entity.constant.Level;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RateConverter {

    public static RateCriteriaResponse toRateCriteriaResponse(List<CategoryGoal> categoryGoals, DailyPlan dailyPlan, Rate rate) {
        Map<Long, Level> categoryRateLevels = getCategoryRateLevels(rate); // 기존 카테고리별 평가 내역 가져오기

        List<CategoryRateResponse> evalCategoryList = createCategoryRateResponseList(categoryGoals, categoryRateLevels);

        Long rateId = (rate != null) ? rate.getId() : null;
        String memo = (rate != null) ? rate.getMemo() : null;
        Level totalLevel = (rate != null) ? rate.getTotalLevel() : null;

        return RateCriteriaResponse.builder()
                .dailyPlanBudget(dailyPlan.getBudget())
                .categoryList(evalCategoryList)
                .dailyTotalCost(dailyPlan.getTotalCost())
                .rateId(rateId)
                .memo(memo)
                .totalLevel(totalLevel)
                .build();
    }

    private static Map<Long, Level> getCategoryRateLevels(Rate rate) {
        return (rate != null) ? rate.getCategoryRates().stream()
                .collect(Collectors.toMap(
                        categoryRate -> categoryRate.getCategoryGoal().getId(),
                        CategoryRate::getLevel
                )) : Collections.emptyMap();
    }

    private static List<CategoryRateResponse> createCategoryRateResponseList(List<CategoryGoal> categoryGoals, Map<Long, Level> categoryRateLevels) {
        return categoryGoals.stream()
                .map(categoryGoal -> CategoryRateResponse.builder()
                        .categoryGoalId(categoryGoal.getId())
                        .categoryName(categoryGoal.getCategory().getName())
                        .level(categoryRateLevels.getOrDefault(categoryGoal.getId(), null))
                        .build())
                .collect(Collectors.toList());
    }

}
