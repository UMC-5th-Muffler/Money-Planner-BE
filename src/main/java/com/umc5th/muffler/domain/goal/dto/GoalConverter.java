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

        // expense를 카테고리 ID별로 그룹화
        Map<Long, List<Expense>> expensesByCategory = new HashMap<>();
        if (expenses != null) {
            for (Expense expense : expenses) {
                expensesByCategory.computeIfAbsent(expense.getCategory().getId(), k -> new ArrayList<>()).add(expense);
            }
        }

        List<CategoryGoalReportDto> categoryGoalReportDtos = createCategoryGoalReports(categoryGoals, expensesByCategory);
        String mostUsedCategory = findMostUsedCategory(categoryGoalReportDtos);

        return GoalReportResponse.builder()
                .goalBudget(goal.getTotalBudget())
                .totalCost(totalCost)
                .dailyAvgCost(dailyAvgCost)
                .mostUsedCategory(mostUsedCategory)
                .categoryReports(categoryGoalReportDtos)
                .zeroDayCount(zeroDayCount)
                .build();
    }

    private static List<CategoryGoalReportDto> createCategoryGoalReports(List<CategoryGoal> categoryGoals, Map<Long, List<Expense>> expensesByCategory) {
        List<CategoryGoalReportDto> categoryGoalReports = new ArrayList<>();
        for (CategoryGoal goal : categoryGoals) {
            List<Expense> matchedExpenses = expensesByCategory.getOrDefault(goal.getCategory().getId(), Collections.emptyList());
            categoryGoalReports.add(createCategoryGoalReport(goal, matchedExpenses));
        }
        return categoryGoalReports;
    }

    private static CategoryGoalReportDto createCategoryGoalReport(CategoryGoal categoryGoal, List<Expense> expenses) {
        long totalCost = 0;
        long mostCost = 0;
        for (Expense expense : expenses) {
            totalCost += expense.getCost();
            mostCost = Math.max(mostCost, expense.getCost());
        }

        long avgCost = expenses.isEmpty() ? 0L : totalCost / expenses.size();

        return CategoryGoalReportDto.builder()
                .categoryBudget(categoryGoal.getBudget())
                .categoryIcon(categoryGoal.getCategory().getIcon())
                .categoryName(categoryGoal.getCategory().getName())
                .totalCost(totalCost)
                .avgCost(avgCost)
                .maxCost(mostCost)
                .expenseCount(expenses.size())
                .build();
    }

    private static String findMostUsedCategory(List<CategoryGoalReportDto> reports) {
        String category = null;
        long maxCount = 0;
        for (CategoryGoalReportDto report : reports) {
            if (report.getExpenseCount() > maxCount) {
                maxCount = report.getExpenseCount();
                category = report.getCategoryName();
            }
        }
        return category;
    }
}
