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

        Map<Long, CategoryGoalReportDto> categoryReportsMap = initCategoryReportsMap(categoryGoals);
        Map<String, Long> categoryTotalCostsMap = new HashMap<>();

        for (Expense expense : expenses) {
            String categoryName = expense.getCategory().getName();
            Long categoryId = expense.getCategory().getId();

            // 카테고리별 총 비용 업데이트
            categoryTotalCostsMap.put(categoryName, categoryTotalCostsMap.getOrDefault(categoryName, 0L) + expense.getCost());

            // 해당 카테고리에 대한 리포트가 존재하는 경우(목표를 세운 카테고리의 경우) 리포트 업데이트
            CategoryGoalReportDto report = categoryReportsMap.get(categoryId);
            if (report != null) {
                report.addExpense(expense.getCost());
            }
        }

        List<CategoryTotalCostDto> categoryTotalCosts = categoryTotalCostsMap.entrySet().stream()
                .map(entry -> new CategoryTotalCostDto(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingLong(CategoryTotalCostDto::getTotalCost).reversed())
                .collect(Collectors.toList());

        List<CategoryGoalReportDto> categoryGoalReports = new ArrayList<>(categoryReportsMap.values());
        categoryGoalReports.sort(Comparator.comparingLong(CategoryGoalReportDto::getTotalCost).reversed());
        for (CategoryGoalReportDto categoryGoalReport : categoryGoalReports) {
            categoryGoalReport.calculateAvgCost();
        }

        String mostUsedCategory = categoryTotalCosts.isEmpty() ? null : categoryTotalCosts.get(0).getCategoryName();

        return GoalReportResponse.builder()
                .goalBudget(goal.getTotalBudget())
                .totalCost(totalCost)
                .dailyAvgCost(dailyAvgCost)
                .mostUsedCategory(mostUsedCategory)
                .categoryTotalCosts(categoryTotalCosts)
                .categoryGoalReports(categoryGoalReports)
                .zeroDayCount(zeroDayCount)
                .build();
    }

    private static Map<Long, CategoryGoalReportDto> initCategoryReportsMap(List<CategoryGoal> categoryGoals) {
        return categoryGoals.stream()
                .collect(Collectors.toMap(
                        categoryGoal -> categoryGoal.getCategory().getId(),
                        categoryGoal -> CategoryGoalReportDto.builder()
                                .categoryName(categoryGoal.getCategory().getName())
                                .categoryIcon(categoryGoal.getCategory().getIcon())
                                .categoryBudget(categoryGoal.getBudget())
                                .build()
                ));
    }

    public static GoalInfo getNowGoalResponse(Goal goal, Long totalCost) {
        return GoalInfo.builder()
                .goalId(goal.getId())
                .goalTitle(goal.getTitle())
                .icon(goal.getIcon())
                .totalBudget(goal.getTotalBudget())
                .totalCost(totalCost)
                .endDate(goal.getEndDate())
                .build();
    }

    public static GoalPreviewResponse getGoalPreviewResponse(Map<Goal, Long> pastInfos, List<Goal> futureGoals, Boolean hasNext) {

        List<GoalInfo> past = pastInfos.entrySet().stream()
                .map(entry -> GoalInfo.builder()
                        .goalId(entry.getKey().getId())
                        .goalTitle(entry.getKey().getTitle())
                        .icon(entry.getKey().getIcon())
                        .totalBudget(entry.getKey().getTotalBudget())
                        .totalCost(entry.getValue())
                        .endDate(entry.getKey().getEndDate())
                        .build())
                .collect(Collectors.toList());

        List<GoalInfo> future = futureGoals.stream()
                .map(goal -> GoalInfo.builder()
                        .goalId(goal.getId())
                        .goalTitle(goal.getTitle())
                        .icon(goal.getIcon())
                        .totalBudget(goal.getTotalBudget())
                        .endDate(goal.getEndDate())
                        .build())
                .collect(Collectors.toList());

        return GoalPreviewResponse.builder()
                .futureGoal(future)
                .endedGoal(past)
                .hasNext(hasNext)
                .build();
    }

    public static GoalListResponse getGoalListResponse(List<Goal> goalList) {

        List<GoalListInfo> info = goalList.stream()
                .map(goal -> GoalListInfo.builder()
                        .goalId(goal.getId())
                        .goalTitle(goal.getTitle())
                        .icon(goal.getIcon())
                        .build())
                .collect(Collectors.toList());

        return GoalListResponse.builder()
                .goalList(info)
                .build();
    }
}
