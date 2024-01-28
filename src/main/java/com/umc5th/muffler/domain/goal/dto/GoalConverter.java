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

    public static GoalPreviewResponse getGoalPreviousResponse(Map<Goal, Long> pastInfos, Goal progressGoal, Long totalCost, List<Goal> futureGoals) {

        List<GoalPreviewInfo> past = pastInfos.entrySet().stream()
                .sorted(Comparator.comparing((Map.Entry<Goal, Long> entry) -> entry.getKey().getStartDate()).reversed())
                .map(entry -> GoalPreviewInfo.builder()
                        .title(entry.getKey().getTitle())
                        .icon(entry.getKey().getIcon())
                        .totalBudget(entry.getKey().getTotalBudget())
                        .totalCost(entry.getValue())
                        .build())
                .collect(Collectors.toList());

        GoalPreviewInfo progress = null;

        if (progressGoal != null) {
            progress = GoalPreviewInfo.builder()
                    .title(progressGoal.getTitle())
                    .icon(progressGoal.getIcon())
                    .totalBudget(progressGoal.getTotalBudget())
                    .totalCost(totalCost)
                    .endDate(progressGoal.getEndDate())
                    .build();
        }

        List<GoalPreviewInfo> future = futureGoals.stream()
                .map(goal -> GoalPreviewInfo.builder()
                        .title(goal.getTitle())
                        .icon(goal.getIcon())
                        .totalBudget(goal.getTotalBudget())
                        .build())
                .collect(Collectors.toList());

        return GoalPreviewResponse.builder()
                .progressGoal(progress)
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
