package com.umc5th.muffler.fixture;

import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.constant.Rate;

import java.time.LocalDate;

public class DailyPlanFixture {
    public static final DailyPlan DAILY_PLAN_ONE = DailyPlan.builder()
            .id(1L)
            .date(LocalDate.of(2024,1,1))
            .budget(5000L)
            .totalCost(1000L)
            .rate(Rate.HIGH)
            .rateMemo("rateMemo")
            .build();

    public static final DailyPlan DAILY_PLAN_NO_RATE = DailyPlan.builder()
            .id(1L)
            .date(LocalDate.of(2024,1,1))
            .budget(5000L)
            .totalCost(1000L)
            .build();

    public static final DailyPlan DAILY_PLAN_TWO = DailyPlan.builder()
            .id(2L)
            .date(LocalDate.of(2024,1,2))
            .budget(5000L)
            .totalCost(2000L)
            .build();

    public static final DailyPlan DAILY_PLAN_THREE = DailyPlan.builder()
            .id(3L)
            .date(LocalDate.now())
            .budget(5000L)
            .totalCost(1000L)
            .rate(Rate.HIGH)
            .build();

    public static final DailyPlan DAILY_PLAN_FOUR = DailyPlan.builder()
            .id(4L)
            .date(LocalDate.now().plusDays(1))
            .budget(5000L)
            .totalCost(2000L)
            .build();

    public static final DailyPlan DAILY_PLAN_FIVE = DailyPlan.builder()
            .id(5L)
            .date(LocalDate.of(2024, 1, 31))
            .budget(5000L)
            .totalCost(1000L)
            .rate(Rate.HIGH)
            .build();

    public static final DailyPlan DAILY_PLAN_SIX = DailyPlan.builder()
            .id(6L)
            .date(LocalDate.of(2024, 2, 1))
            .budget(5000L)
            .totalCost(2000L)
            .build();
}
