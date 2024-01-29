package com.umc5th.muffler.fixture;

import com.umc5th.muffler.entity.Goal;
import java.time.LocalDate;
import java.util.List;

public class GoalFixture {
    public static Goal create() {
        return Goal.builder()
                .id(1L)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 2))
                .title("title")
                .memo("memo")
                .icon("icon")
                .totalBudget(10000L)
                .categoryGoals(List.of(CategoryGoalFixture.CATEGORY_GOAL_ONE, CategoryGoalFixture.CATEGORY_GOAL_TWO))
                .dailyPlans(List.of(DailyPlanFixture.DAILY_PLAN_ONE, DailyPlanFixture.DAILY_PLAN_TWO))
                .build();
    }

    public static Goal createWithoutRate(){
        return Goal.builder()
                .id(1L)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 2))
                .title("title")
                .memo("memo")
                .icon("icon")
                .totalBudget(10000L)
                .categoryGoals(List.of(CategoryGoalFixture.CATEGORY_GOAL_ONE, CategoryGoalFixture.CATEGORY_GOAL_TWO))
                .dailyPlans(List.of(DailyPlanFixture.DAILY_PLAN_NO_RATE, DailyPlanFixture.DAILY_PLAN_TWO))
                .build();
    }

    public static Goal createWithoutDailyPlans(){
        return Goal.builder()
                .id(1L)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 2))
                .title("title")
                .memo("memo")
                .icon("icon")
                .totalBudget(10000L)
                .categoryGoals(List.of(CategoryGoalFixture.CATEGORY_GOAL_ONE))
                .dailyPlans(List.of(DailyPlanFixture.DAILY_PLAN_TWO))
                .build();
    }

    public static Goal createWithoutCategoryGoals(){
        return Goal.builder()
                .id(1L)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 2))
                .title("title")
                .memo("memo")
                .icon("icon")
                .totalBudget(10000L)
                .dailyPlans(List.of(DailyPlanFixture.DAILY_PLAN_NO_RATE, DailyPlanFixture.DAILY_PLAN_TWO))
                .build();
    }

    public static Goal createToday() {
        return Goal.builder()
                .id(2L)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .title("title")
                .memo("memo")
                .icon("icon")
                .totalBudget(10000L)
                .categoryGoals(List.of(CategoryGoalFixture.CATEGORY_GOAL_ONE, CategoryGoalFixture.CATEGORY_GOAL_TWO))
                .dailyPlans(List.of(DailyPlanFixture.DAILY_PLAN_THREE, DailyPlanFixture.DAILY_PLAN_FOUR))
                .build();
    }

    public static Goal createMonth() {
        return Goal.builder()
                .id(3L)
                .startDate(LocalDate.of(2024, 1, 31))
                .endDate(LocalDate.of(2024, 2, 1))
                .title("title")
                .memo("memo")
                .icon("icon")
                .totalBudget(10000L)
                .categoryGoals(List.of(CategoryGoalFixture.CATEGORY_GOAL_ONE, CategoryGoalFixture.CATEGORY_GOAL_TWO))
                .dailyPlans(List.of(DailyPlanFixture.DAILY_PLAN_FIVE, DailyPlanFixture.DAILY_PLAN_SIX))
                .build();
    }
}
