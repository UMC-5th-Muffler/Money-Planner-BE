package com.umc5th.muffler.global.util;

import com.umc5th.muffler.entity.Routine;
import java.time.LocalDate;
import java.util.List;

public interface RoutineProcessor {
    boolean isRoutineDay(LocalDate date, Routine routine);
    List<LocalDate> getRoutineDates(LocalDate startDate, LocalDate endDate, Routine routine);
}
