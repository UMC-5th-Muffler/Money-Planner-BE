package com.umc5th.muffler.fixture;

import com.umc5th.muffler.domain.dailyplan.dto.DailyInfo;
import com.umc5th.muffler.domain.dailyplan.dto.GoalInfo;
import com.umc5th.muffler.domain.dailyplan.dto.InactiveDaily;
import com.umc5th.muffler.domain.dailyplan.dto.WholeCalendar;
import com.umc5th.muffler.entity.constant.Rate;
import java.time.LocalDate;
import java.util.List;

public class HomeFixture {

    public static WholeCalendar createWholeCalendar() {
        return WholeCalendar.builder()
                .dailyList(createDailyList())
                .build();
    }

    public static GoalInfo createGoalInfo() {
        return GoalInfo.builder()
                .goalId(1L)
                .goalTitle("GoalTitle")
                .goalBudget(1000L)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 10))
                .totalCost(100L)
                .build();
    }

    public static List<DailyInfo> createDailyList() {
        return List.of((DailyInfo)
                new InactiveDaily(LocalDate.of(2024, 1, 1), Rate.HIGH)
        );
    }
}
