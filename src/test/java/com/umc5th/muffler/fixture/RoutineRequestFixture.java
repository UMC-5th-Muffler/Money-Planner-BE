package com.umc5th.muffler.fixture;

import static com.umc5th.muffler.entity.constant.RoutineType.MONTHLY;
import static com.umc5th.muffler.entity.constant.RoutineType.WEEKLY;
import static java.time.DayOfWeek.MONDAY;

import com.umc5th.muffler.domain.routine.dto.RoutineRequest;
import java.time.LocalDate;
import java.util.List;

public class RoutineRequestFixture {
    public static RoutineRequest createWeekly() {
        return RoutineRequest.builder()
                .type(WEEKLY)
                .endDate(LocalDate.of(2024, 12, 12))
                .weeklyRepeatDays(List.of(MONDAY))
                .weeklyTerm("1")
                .build();
    }

    public static RoutineRequest createMonthly() {
        return RoutineRequest.builder()
                .type(MONTHLY)
                .endDate(LocalDate.of(2024, 12, 12))
                .monthlyRepeatDay("1")
                .build();
    }

}
