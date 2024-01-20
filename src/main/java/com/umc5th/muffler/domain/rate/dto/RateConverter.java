package com.umc5th.muffler.domain.rate.dto;

import com.umc5th.muffler.entity.*;
import com.umc5th.muffler.entity.constant.Level;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.GoalException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
        return categoryGoals.stream()
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

    public static List<CategoryRate> toCategoryRates(RateCreateRequest request, Goal goal){
        List<CategoryGoal> categoryGoals = Optional.ofNullable(goal.getCategoryGoals())
                .orElse(Collections.emptyList());
        List<CategoryRate> categoryRates = new ArrayList<>();

        for (CategoryRateCreateRequest categoryRateCreateRequest : request.getCategoryRateList()) {
            CategoryGoal categoryGoal = categoryGoals.stream()
                    .filter(CategoryGoal -> CategoryGoal.getId().equals(categoryRateCreateRequest.getCategoryGoalId()))
                    .findAny()
                    .orElseThrow(() -> new GoalException(ErrorCode.CATEGORY_GOAL_NOT_FOUND));

            CategoryRate categoryRate = RateConverter.toCategoryRate(categoryRateCreateRequest, categoryGoal);
            categoryRates.add(categoryRate);
        }

        return categoryRates;
    }

    public static Rate toRate(RateCreateRequest request){
        return Rate.builder()
                .totalLevel(Level.valueOf(request.getTotalLevel()))
                .memo(request.getMemo())
                .build();
    }

    public static CategoryRate toCategoryRate(CategoryRateCreateRequest categoryRateCreateRequest, CategoryGoal categoryGoal){
        return CategoryRate.builder()
                .level(Level.valueOf(categoryRateCreateRequest.getLevel()))
                .categoryGoal(categoryGoal)
                .build();
    }

}
