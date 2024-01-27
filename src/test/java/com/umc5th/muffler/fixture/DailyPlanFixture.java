package com.umc5th.muffler.fixture;

import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.constant.Level;

import java.time.LocalDate;

public class DailyPlanFixture {
    public static final DailyPlan DAILY_PLAN_ONE = DailyPlan.builder()
            .id(1L)
            .date(LocalDate.of(2024,1,1))
            .budget(5000L)
            .totalCost(1000L)
            .rate(Level.HIGH)
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

}
