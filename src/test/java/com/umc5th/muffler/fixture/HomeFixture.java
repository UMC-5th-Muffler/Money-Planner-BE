package com.umc5th.muffler.fixture;

import com.umc5th.muffler.domain.dailyplan.dto.ActiveGoalResponse;
import com.umc5th.muffler.domain.dailyplan.dto.CategoryCalendar;
import com.umc5th.muffler.domain.dailyplan.dto.GoalDailyInfo;
import com.umc5th.muffler.domain.dailyplan.dto.WholeCalendar;
import com.umc5th.muffler.entity.constant.Rate;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HomeFixture {

    public static WholeCalendar createWholeCalendar() {
        return WholeCalendar.builder()
                .activeGoalResponse(createActiveGoalResponse())
                .build();
    }

    public static ActiveGoalResponse createActiveGoalResponse() {
        return ActiveGoalResponse.builder()
                .goalId(1L)
                .goalTitle("GoalTitle")
                .goalBudget(1000L)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 10))
                .totalCost(100L)
                .dailyList(createDailyInfoList())
                .build();
    }

    public static List<GoalDailyInfo> createDailyInfoList() {
        return IntStream.range(0, 2)
                .mapToObj(i -> GoalDailyInfo.builder()
                        .dailyBudget(5000L)
                        .dailyTotalCost(4000L)
                        .dailyRate(Rate.MEDIUM)
                        .isZeroDay(false)
                        .build())
                .collect(Collectors.toList());
    }

    public static CategoryCalendar createCategoryCalendar() {
        return CategoryCalendar.builder()
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 10))
                .categoryId(1L)
                .categoryName("Acategory")
                .categoryTotalCost(10L)
                .categoryDailyCost(List.of(1L, 1L))
                .build();
    }

}
