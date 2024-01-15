package com.umc5th.muffler.fixture;

import com.umc5th.muffler.domain.goal.dto.CategoryGoalRequest;
import com.umc5th.muffler.domain.goal.dto.GoalCreateRequest;
import java.time.LocalDate;
import java.util.List;

public class GoalCreateRequestFixture {
    public static GoalCreateRequest create() {
        return GoalCreateRequest.builder()
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 2))
                .title("title")
                .detail("detail")
                .icon("icon")
                .totalBudget(10000L)
                .categoryGoals(List.of(new CategoryGoalRequest(1L, 1000L)))
                .dailyBudgets(List.of(5000L, 5000L))
                .build();
    }

    public static GoalCreateRequest create(LocalDate startDate, LocalDate endDate) {
        return GoalCreateRequest.builder()
                .startDate(startDate)
                .endDate(endDate)
                .title("title")
                .detail("detail")
                .icon("icon")
                .totalBudget(10000L)
                .categoryGoals(List.of(new CategoryGoalRequest(1L, 1000L)))
                .dailyBudgets(List.of(5000L, 5000L))
                .build();
    }

    public static GoalCreateRequest createDuplicatedCategoryGoals() {
        return GoalCreateRequest.builder()
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 2))
                .title("title")
                .detail("detail")
                .icon("icon")
                .totalBudget(10000L)
                .categoryGoals(List.of(new CategoryGoalRequest(1L, 100L), new CategoryGoalRequest(1L, 100L)))
                .dailyBudgets(List.of(5000L, 5000L))
                .build();
    }

    public static GoalCreateRequest createInvalidCategoryBudget() {
        return GoalCreateRequest.builder()
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 2))
                .title("title")
                .detail("detail")
                .icon("icon")
                .totalBudget(10000L)
                .categoryGoals(List.of(new CategoryGoalRequest(1L, 20000L)))
                .dailyBudgets(List.of(5000L, 5000L))
                .build();
    }

    public static GoalCreateRequest createInvalidDailyPlanPeriod() {
        return GoalCreateRequest.builder()
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 2))
                .title("title")
                .detail("detail")
                .icon("icon")
                .totalBudget(10000L)
                .categoryGoals(List.of(new CategoryGoalRequest(1L, 20000L)))
                .dailyBudgets(List.of(5000L))
                .build();
    }

    public static GoalCreateRequest createInvalidDailyBudgetSum() {
        return GoalCreateRequest.builder()
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 2))
                .title("title")
                .detail("detail")
                .icon("icon")
                .totalBudget(10000L)
                .categoryGoals(List.of(new CategoryGoalRequest(1L, 20000L)))
                .dailyBudgets(List.of(5000L, 1000L))
                .build();
    }
}
