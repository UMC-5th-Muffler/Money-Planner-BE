package com.umc5th.muffler.fixture;

import com.umc5th.muffler.domain.goal.dto.GoalTerm;

import java.time.LocalDate;

public class GoalTermFixture {

    public static GoalTerm create() {
        LocalDate startDate = LocalDate.of(2023, 12, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 8);
        return new GoalTerm(startDate, endDate);
    }
}
