package com.umc5th.muffler.fixture;

import com.umc5th.muffler.entity.Goal;
import java.time.LocalDate;

public class GoalEntityFixture {
    public static Goal create() {
        return Goal.builder()
                .id(1L)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 2))
                .title("title")
                .detail("detail")
                .icon("icon")
                .totalBudget(10000L)
                .build();
    }
}
