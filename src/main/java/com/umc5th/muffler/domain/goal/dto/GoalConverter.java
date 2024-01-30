package com.umc5th.muffler.domain.goal.dto;

import com.umc5th.muffler.entity.Goal;

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

    public static GoalInfo getNowGoalResponse(Goal goal, Long totalCost) {
        return GoalInfo.builder()
                .title(goal.getTitle())
                .icon(goal.getIcon())
                .totalBudget(goal.getTotalBudget())
                .totalCost(totalCost)
                .endDate(goal.getEndDate())
                .build();
    }

    public static GoalPreviewResponse getGoalPreviewResponse(Map<Goal, Long> pastInfos, List<Goal> futureGoals) {

        List<GoalInfo> past = pastInfos.entrySet().stream()
                .sorted(Comparator.comparing((Map.Entry<Goal, Long> entry) -> entry.getKey().getStartDate()).reversed())
                .map(entry -> GoalInfo.builder()
                        .title(entry.getKey().getTitle())
                        .icon(entry.getKey().getIcon())
                        .totalBudget(entry.getKey().getTotalBudget())
                        .totalCost(entry.getValue())
                        .build())
                .collect(Collectors.toList());

        List<GoalInfo> future = futureGoals.stream()
                .map(goal -> GoalInfo.builder()
                        .title(goal.getTitle())
                        .icon(goal.getIcon())
                        .totalBudget(goal.getTotalBudget())
                        .build())
                .collect(Collectors.toList());

        return GoalPreviewResponse.builder()
                .futureGoal(future)
                .endedGoal(past)
                .build();
    }

    public static GoalListResponse getGoalListResponse(List<Goal> goalList) {

        List<GoalListInfo> info = goalList.stream()
                .map(goal -> GoalListInfo.builder()
                        .title(goal.getTitle())
                        .icon(goal.getIcon())
                        .build())
                .collect(Collectors.toList());

        return GoalListResponse.builder()
                .goalList(info)
                .build();
    }
}
