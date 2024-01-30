package com.umc5th.muffler.domain.goal.dto;

import com.umc5th.muffler.entity.CategoryGoal;
import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Goal;

import java.util.*;
import java.util.stream.Collectors;

public class GoalConverter {
    public static GoalPreviousResponse getGoalPreviousResponse(List<Goal> goals) {
        return new GoalPreviousResponse(
                goals.stream()
                        .sorted(Comparator.comparing(Goal::getStartDate).reversed())
                        .map(goal -> new GoalTerm(goal.getStartDate(), goal.getEndDate()))
                        .collect(Collectors.toList())
        );
    }

    public static GoalGetResponse getGoalWithTotalCostResponse(Goal goal, List<DailyPlan> dailyPlans){
        long totalCost = dailyPlans.stream().mapToLong(DailyPlan::getTotalCost).sum();

        return GoalGetResponse.builder()
                .totalBudget(goal.getTotalBudget())
                .title(goal.getTitle())
                .startDate(goal.getStartDate())
                .endDate(goal.getEndDate())
                .icon(goal.getIcon())
                .totalCost(totalCost)
                .build();
    }

    public static GoalReportResponse getGoalReportResponse(Goal goal, List<CategoryGoal> categoryGoals, List<DailyPlan> dailyPlans, List<Expense> expenses) {
        // zeroDayCount와 totalCost 계산
        long zeroDayCount = 0;
        long totalCost = 0;
        for (DailyPlan dailyPlan : dailyPlans) {
            if (dailyPlan.getIsZeroDay()) {
                zeroDayCount++;
            }
            totalCost += dailyPlan.getTotalCost();
        }
        long dailyAvgCost = dailyPlans.isEmpty() ? 0 : totalCost / dailyPlans.size();

        // CategoryGoalReportDto를 카테고리 ID별로 그룹화하여 초기화
        Map<Long, CategoryGoalReportDto> categoryReportsMap = initCtegoryReportsMap(categoryGoals);
        // expenses 순회하면서 categoryReportsMap 업데이트
        updateCategoryReports(categoryReportsMap, expenses);
        // 카테고리별 avgCost 계산
        for (CategoryGoalReportDto report : categoryReportsMap.values()) {
            report.calculateAvgCost();
        }

        // totalCost가 높은 순서로 카테고리 리포트 정렬
        List<CategoryGoalReportDto> sortedCategoryReports = new ArrayList<>(categoryReportsMap.values());
        sortedCategoryReports.sort(Comparator.comparingLong(CategoryGoalReportDto::getTotalCost).reversed());

        String mostUsedCategory = sortedCategoryReports.isEmpty() ? null : sortedCategoryReports.get(0).getCategoryName();

        return GoalReportResponse.builder()
                .goalBudget(goal.getTotalBudget())
                .totalCost(totalCost)
                .dailyAvgCost(dailyAvgCost)
                .mostUsedCategory(mostUsedCategory)
                .categoryReports(sortedCategoryReports)
                .zeroDayCount(zeroDayCount)
                .build();
    }

    private static Map<Long, CategoryGoalReportDto> initCtegoryReportsMap(List<CategoryGoal> categoryGoals) {
        return categoryGoals.stream()
                .collect(Collectors.toMap(
                        categoryGoal -> categoryGoal.getCategory().getId(),
                        categoryGoal -> new CategoryGoalReportDto(
                                categoryGoal.getCategory().getName(),
                                categoryGoal.getCategory().getIcon(),
                                categoryGoal.getBudget(),
                                0L, 0L, 0L, 0)
                ));
    }

    private static void updateCategoryReports(Map<Long, CategoryGoalReportDto> categoryReportsMap, List<Expense> expenses) {
        if (expenses != null) {
            for (Expense expense : expenses) {
                CategoryGoalReportDto report = categoryReportsMap.get(expense.getCategory().getId());
                if (report != null) {
                    report.addExpense(expense.getCost());
                }
            }
        }
    }
}
