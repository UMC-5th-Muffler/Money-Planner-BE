package com.umc5th.muffler.fixture;

import com.umc5th.muffler.entity.Routine;
import com.umc5th.muffler.entity.WeeklyRepeatDay;
import java.time.DayOfWeek;

public class WeeklyRepeatDayFixture {
    public static WeeklyRepeatDay of(Routine routine, DayOfWeek dayOfWeek) {
        return WeeklyRepeatDay.builder()
                .routine(routine)
                .dayOfWeek(dayOfWeek)
                .build();
    }
}
