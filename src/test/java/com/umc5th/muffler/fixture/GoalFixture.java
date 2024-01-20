package com.umc5th.muffler.fixture;

import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.CategoryGoal;
import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.constant.Status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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
                .build();
    }

    public static Goal createDetail() {

        Category category = CategoryFixture.CATEGORY_ZERO;
        CategoryGoal categoryGoal = CategoryGoal.of(category, 1000L);

        DailyPlan dailyPlan1 = DailyPlan.builder()
                .date(LocalDate.of(2024, 1, 1))
                .budget(5000L)
                .totalCost(1000L)
                .build();
        DailyPlan dailyPlan2 = DailyPlan.builder()
                .date(LocalDate.of(2024, 1, 2))
                .budget(5000L)
                .totalCost(1000L)
                .build();

        return Goal.builder()
                .id(1L)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 2))
                .title("title")
                .memo("memo")
                .icon("icon")
                .totalBudget(10000L)
                .categoryGoals(new ArrayList<>(Arrays.asList(categoryGoal)))
                .dailyPlans(new ArrayList<>(Arrays.asList(dailyPlan1, dailyPlan2)))
                .build();
    }
}
