package com.umc5th.muffler.fixture;

import com.umc5th.muffler.entity.Routine;
import com.umc5th.muffler.entity.WeeklyRepeatDay;
import java.time.DayOfWeek;

public class WeeklyRepeatDayFixture {
    public static WeeklyRepeatDay MONDAY_REPEAT(Routine routine) {
        return WeeklyRepeatDay.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .routine(routine)
                .build();
    }
}
