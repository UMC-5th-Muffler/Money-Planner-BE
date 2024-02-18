package com.umc5th.muffler.domain.dailyplan.repository;

import com.querydsl.core.Tuple;
import com.umc5th.muffler.domain.dailyplan.dto.DailyPlanAlarm;
import com.umc5th.muffler.entity.constant.Rate;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DailyPlanRepositoryCustom {
    List<Tuple> findDateAndRateByGoalAndDateRange(Long goalId, LocalDate startDate, LocalDate endDate);
    Map<LocalDate, Rate> findByGoalAndDateRangeGroupedByDate(Long goalId, LocalDate startDate, LocalDate endDate);
    List<DailyPlanAlarm> findDailyPlanAlarms(LocalDate date);
}
