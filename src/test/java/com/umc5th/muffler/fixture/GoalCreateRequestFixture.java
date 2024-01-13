package com.umc5th.muffler.fixture;

import com.umc5th.muffler.domain.goal.dto.GoalCreateRequest;
import java.time.LocalDate;
import java.util.List;

public class GoalCreateRequestFixture {

    public static GoalCreateRequest create() {
        return new GoalCreateRequest(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 2),
                "title",
                "detail",
                "icon",
                10000L,
                List.of(5000L, 5000L)
        );
    }

    public static GoalCreateRequest create(LocalDate startDate, LocalDate endDate) {
        return new GoalCreateRequest(
                startDate,
                endDate,
                "title",
                "detail",
                "icon",
                10000L,
                List.of(10000L)
        );
    }
}
