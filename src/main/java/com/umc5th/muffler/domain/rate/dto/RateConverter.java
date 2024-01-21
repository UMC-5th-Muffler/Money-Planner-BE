package com.umc5th.muffler.domain.rate.dto;

import com.umc5th.muffler.entity.CategoryGoal;
import com.umc5th.muffler.entity.CategoryRate;
import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Rate;
import com.umc5th.muffler.entity.constant.Level;
import com.umc5th.muffler.entity.constant.Status;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RateConverter {

    public static RateCriteriaResponse toRateCriteriaResponse(List<CategoryGoal> categoryGoals, DailyPlan dailyPlan, Rate rate) {

        List<CategoryRateResponse> evalCategoryList = createCategoryRateResponseList(categoryGoals, rate);

        Long rateId = (rate != null) ? rate.getId() : null;
        String memo = (rate != null) ? rate.getMemo() : null;
        Level totalLevel = (rate != null) ? rate.getTotalLevel() : null;

        return RateCriteriaResponse.builder()
                .dailyPlanBudget(dailyPlan.getBudget())
                .categoryRateList(evalCategoryList)
                .dailyTotalCost(dailyPlan.getTotalCost())
                .rateId(rateId)
                .memo(memo)
                .totalLevel(totalLevel)
                .build();
    }

    private static List<CategoryRateResponse> createCategoryRateResponseList(List<CategoryGoal> categoryGoals, Rate rate) {
        if (categoryGoals == null) {
            return Collections.emptyList();
        }

        return categoryGoals.stream()
                .filter(categoryGoal -> categoryGoal.getCategory().getStatus() == Status.ACTIVE)
                .sorted(Comparator.comparingLong(categoryGoal -> categoryGoal.getCategory().getPriority()))
                .map(categoryGoal -> {
                    CategoryRate categoryRate = findMatchingCategoryRate(rate, categoryGoal.getId());
                    return CategoryRateResponse.builder()
                            .categoryGoalId(categoryGoal.getId())
                            .categoryName(categoryGoal.getCategory().getName())
                            .level(categoryRate != null ? categoryRate.getLevel() : null)
                            .categoryRateId(categoryRate != null ? categoryRate.getId() : null)
                            .build();
                })
                .collect(Collectors.toList());

    }

    private static CategoryRate findMatchingCategoryRate(Rate rate, Long categoryGoalId) {
        if (rate == null || rate.getCategoryRates() == null) {
            return null;
        }
        return rate.getCategoryRates().stream()
                .filter(categoryRate -> categoryRate.getCategoryGoal().getId().equals(categoryGoalId))
                .findAny()
                .orElse(null);
    }


    public static Rate toRate(RateCreateRequest request){
        return Rate.builder()
                .totalLevel(Level.valueOf(request.getTotalLevel()))
                .memo(request.getMemo())
                .build();
    }

    public static CategoryRate toCategoryRate(CategoryRateCreateRequest request, CategoryGoal categoryGoal){
        return CategoryRate.builder()
                .level(Level.valueOf(request.getLevel()))
                .categoryGoal(categoryGoal)
                .build();
    }

    public static CategoryRate toCategoryRate(CategoryRateUpdateRequest request, CategoryGoal categoryGoal){
        return CategoryRate.builder()
                .level(Level.valueOf(request.getLevel()))
                .categoryGoal(categoryGoal)
                .build();
    }

}
