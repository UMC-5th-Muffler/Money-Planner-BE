package com.umc5th.muffler.domain.dailyplan.repository;

import com.querydsl.core.Tuple;
import java.time.LocalDate;
import java.util.List;

public interface DailyPlanRepositoryCustom {
    List<Tuple> findDateAndRateByGoalAndDateRange(Long goalId, LocalDate startDate, LocalDate endDate);
}
