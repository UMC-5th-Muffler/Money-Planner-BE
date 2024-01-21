package com.umc5th.muffler.fixture;

import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.Member;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

    public static Goal createGoalRegardlessOfBudget(Member member, LocalDate startDate, LocalDate endDate) {
        Goal goal = Goal.builder()
                .startDate(startDate)
                .endDate(endDate)
                .title("title")
                .memo("memo")
                .icon("icon")
                .totalBudget(700L)
                .member(member)
                .build();
        for (LocalDate date = startDate; !date.isAfter(endDate) ; date = date.plusDays(1)) {
            DailyPlan dailyPlan = DailyPlan.builder()
                    .goal(goal)
                    .date(date)
                    .budget(1L)
                    .build();
            goal.addDailyPlan(dailyPlan);
        }
        return goal;
    }
}
