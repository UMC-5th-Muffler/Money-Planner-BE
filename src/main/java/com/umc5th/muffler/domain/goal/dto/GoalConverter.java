package com.umc5th.muffler.domain.goal.dto;

import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Goal;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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

    public static GoalPreviewResponse getGoalPreviousResponse(Map<Goal, Long> pastInfos, Goal progressGoal, Long totalCost, List<Goal> futureGoals) {

        List<GoalPreviewInfo> past = pastInfos.entrySet().stream()
                .map(entry -> GoalPreviewInfo.builder()
                        .title(entry.getKey().getTitle())
                        .icon(entry.getKey().getIcon())
                        .totalBudget(entry.getKey().getTotalBudget())
                        .totalCost(entry.getValue())
                        .endDate(entry.getKey().getEndDate())
                        .build())
                .collect(Collectors.toList());

        GoalPreviewInfo progress = GoalPreviewInfo.builder()
                .title(progressGoal.getTitle())
                .icon(progressGoal.getIcon())
                .totalBudget(progressGoal.getTotalBudget())
                .totalCost(totalCost)
                .endDate(progressGoal.getEndDate())
                .build();

        List<GoalPreviewInfo> future = futureGoals.stream()
                .map(goal -> GoalPreviewInfo.builder()
                        .title(goal.getTitle())
                        .icon(goal.getIcon())
                        .totalBudget(goal.getTotalBudget())
                        .build())
                .collect(Collectors.toList());

        return GoalPreviewResponse.builder()
                .progressGoal(progress)
                .endedGoal(past)
                .futureGoal(future)
                .build();
    }
}
