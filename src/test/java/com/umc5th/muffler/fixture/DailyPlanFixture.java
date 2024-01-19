package com.umc5th.muffler.fixture;

import com.umc5th.muffler.entity.DailyPlan;

import java.time.LocalDate;

public class DailyPlanFixture {
    public static final DailyPlan DAILY_PLAN_ONE = DailyPlan.builder()
            .id(1L)
            .date(LocalDate.of(2024,1,1))
            .budget(5000L)
            .totalCost(1000L)
            .rate(RateFixture.RATE_ONE)
            .build();

    public static final DailyPlan DAILY_PLAN_TWO = DailyPlan.builder()
            .id(2L)
            .date(LocalDate.of(2024,1,2))
            .budget(5000L)
            .totalCost(2000L)
            .build();

}
