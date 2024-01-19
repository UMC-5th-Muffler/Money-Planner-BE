package com.umc5th.muffler.domain.rate.dto;

import com.umc5th.muffler.entity.*;
import com.umc5th.muffler.entity.constant.Level;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.GoalException;

import java.util.ArrayList;
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

    public static List<CategoryRate> toCategoryRates(RateCreateRequest request, Goal goal){
        List<CategoryGoal> categoryGoals = goal.getCategoryGoals();
        List<CategoryRate> categoryRates = new ArrayList<>();

        for (CategoryRateRequest categoryRateRequest : request.getCategoryRateList()) {
            CategoryGoal categoryGoal = categoryGoals.stream()
                    .filter(CategoryGoal -> CategoryGoal.getId().equals(categoryRateRequest.getCategoryGoalId()))
                    .findAny()
                    .orElseThrow(() -> new GoalException(ErrorCode.CATEGORY_GOAL_NOT_FOUND));

            CategoryRate categoryRate = RateConverter.toCategoryRate(categoryRateRequest, categoryGoal);
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

    public static CategoryRate toCategoryRate(CategoryRateRequest categoryRateRequest, CategoryGoal categoryGoal){
        return CategoryRate.builder()
                .level(Level.valueOf(categoryRateRequest.getLevel()))
                .categoryGoal(categoryGoal)
                .build();
    }
}
