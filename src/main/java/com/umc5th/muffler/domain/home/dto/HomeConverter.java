package com.umc5th.muffler.domain.home.dto;

import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.constant.Level;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HomeConverter {

    public static WholeCalendarResponse toWholeCalendar(LocalDate date, Goal goal, Long totalCost, Map<Category, Long> categoryList, List<Long> dailyBudgetList, List<Long> dailyTotalCostList) {

        List<CategoryInfoDto> categoryInfoList = categoryList
                .entrySet()
                .stream()
                .map(entry -> CategoryInfoDto.builder()
                        .id(entry.getKey().getId())
                        .name(entry.getKey().getName())
                        .categoryGoalId(entry.getValue())
                        .build())
                .collect(Collectors.toList());

        List<DailyInfoDto> dailyInfoList = new ArrayList<>();
        for (int i = 0; i < dailyBudgetList.size(); i++) {
            Long dailyBudget = dailyBudgetList.get(i);
            Long dailyTotalCost = i < dailyTotalCostList.size() ? dailyTotalCostList.get(i) : 0L;
//            Level dailyRate = i < dailyRateList.size() ? dailyRateList.get(i) : null;
            Level dailyRate = Level.HIGH; // 임시

            DailyInfoDto dailyInfo = DailyInfoDto.builder()
                    .dailyBudget(dailyBudget)
                    .dailyTotalCost(dailyTotalCost)
                    .dailyRate(dailyRate)
                    .build();

            dailyInfoList.add(dailyInfo);
        }

        return WholeCalendarResponse.builder()
                .calendarDate(date)
                .goalId(goal.getId())
                .goalTitle(goal.getTitle())
                .goalBudget(goal.getTotalBudget())
                .goalStartDate(goal.getStartDate())
                .goalEndDate(goal.getEndDate())
                .totalCost(totalCost)
                .categoryList(categoryInfoList)
                .dailyList(dailyInfoList)
                .build();
    }

    public static CategoryGoalCalendarResponse toGoalCalendar(Long categoryBudget, Long categoryTotalCost, List<Long> dailyTotalCostList) {

        List<DailyCategoryInfoDto> dailyCategoryInfoList = new ArrayList<>();
        for(int i = 0; i < dailyTotalCostList.size(); i++) {
            Long dailyTotalCost = i < dailyTotalCostList.size() ? dailyTotalCostList.get(i) : 0L;
//            Level dailyRate = i < dailyRateList.size() ? dailyRateList.get(i) : null;
            Level dailyRate = Level.HIGH; // 임시

            DailyCategoryInfoDto dailyCategoryInfo = DailyCategoryInfoDto.builder()
                    .dailyTotalCost(dailyTotalCost)
                    .dailyRate(dailyRate)
                    .build();

            dailyCategoryInfoList.add(dailyCategoryInfo);
        }

        return CategoryGoalCalendarResponse.builder()
                .categoryBudget(categoryBudget)
                .categoryTotalCost(categoryTotalCost)
                .dailyList(dailyCategoryInfoList)
                .build();
    }

    public static CategoryNoGoalCalendarResponse toNoGoalCalendar(List<Long> dailyTotalCostList) {
        return CategoryNoGoalCalendarResponse.builder()
                .dailyCostList(dailyTotalCostList)
                .build();
    }
}
