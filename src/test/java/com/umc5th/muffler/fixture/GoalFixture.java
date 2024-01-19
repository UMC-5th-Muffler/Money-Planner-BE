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
                .categoryGoals(List.of(CategoryGoalFixture.create()))
                .dailyPlans(List.of(DailyPlanFixture.DAILY_PLAN_ONE, DailyPlanFixture.DAILY_PLAN_TWO))
                .build();
    }
}
